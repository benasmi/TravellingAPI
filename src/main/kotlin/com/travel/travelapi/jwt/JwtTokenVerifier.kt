package com.travel.travelapi.jwt

import com.travel.travelapi.controllers.AuthController
import com.travel.travelapi.exceptions.InvalidUserDataException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.stream.Collectors
import javax.crypto.SecretKey
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JwtTokenVerifier(private val secretKey: SecretKey,
                       private val jwtConfig: JwtConfig,
                       private val authController: AuthController) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val authorizationHeader = request.getHeader(jwtConfig.authorizationHeader)
        if (authorizationHeader.isNullOrEmpty() || !authorizationHeader.startsWith(jwtConfig.tokenPrefix!!)) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authorizationHeader.replace(jwtConfig.tokenPrefix!!, "")

        try {
            val claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            val body = claimsJws.body
            val username = body.subject
            val type = body["type"] as String

            if(type != JwtConfig.JwtTypes.ACCESS_TOKEN.name){
                throw InvalidUserDataException(String.format("Token %s is not type of ACCESS_TOKEN", token))
            }

            val authorities = authController.getPermissionsByIdentifier(username)

            val authentication: Authentication = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            )
            SecurityContextHolder.getContext().authentication = authentication
        } catch (e: JwtException) {
            throw IllegalStateException(String.format("Token %s cannot be trusted", token))
        }
        filterChain.doFilter(request, response)
    }

}