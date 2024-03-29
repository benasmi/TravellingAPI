package com.travel.travelapi.jwt

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.controllers.AuthController
import com.travel.travelapi.exceptions.InvalidUserDataException
import com.travel.travelapi.utils.DeviceType
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.hibernate.validator.internal.util.logging.LoggerFactory
import org.springframework.mobile.device.Device
import org.springframework.mobile.device.DeviceUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.lang.System.getLogger
import java.util.logging.Logger
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

        val device = DeviceType.getDevice(request)

        val token = authorizationHeader.replace(jwtConfig.tokenPrefix!!, "")
        try {
            val claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            val body = claimsJws.body
            val username = body.subject
            val type = body["type"] as String
            val provider = body["provider"] as String

            if(type != JwtConfig.JwtTypes.ACCESS_TOKEN.name){
                throw InvalidUserDataException(String.format("Token %s is not type of ACCESS_TOKEN", token))
            }

            val user = authController.getUserByIdentifier(username, provider) ?: throw InvalidUserDataException("User invalid")
            val principal = TravelUserDetails.create(user)
            principal.device = device

            val authentication: Authentication = UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    principal.authorities
            )

            SecurityContextHolder.getContext().authentication = authentication
        } catch (e: JwtException) {
            throw IllegalStateException(String.format("Token %s cannot be trusted", token))
        }
        filterChain.doFilter(request, response)
    }
}