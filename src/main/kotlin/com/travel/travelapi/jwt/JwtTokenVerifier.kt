package com.travel.travelapi.jwt

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
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
                       private val jwtConfig: JwtConfig) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val authorizationHeader = request.getHeader(jwtConfig.authorizationHeader)
        if (authorizationHeader.isNullOrEmpty() || !authorizationHeader.startsWith(jwtConfig.tokenPrefix!!)) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authorizationHeader.replace(jwtConfig.tokenPrefix!!, "")
        try {
            val claimsJws = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
            val body = claimsJws.body
            val username = body.subject
            val authorities = body["authorities"] as List<Map<String, String>>?

            val simpleGrantedAuthorities = authorities!!.stream()
                    .map { m: Map<String, String> -> SimpleGrantedAuthority(m["authority"]) }
                    .collect(Collectors.toSet())

            val authentication: Authentication = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    simpleGrantedAuthorities
            )

            SecurityContextHolder.getContext().authentication = authentication
        } catch (e: JwtException) {
            throw IllegalStateException(String.format("Token %s cannot be trusted", token))
        } 
        filterChain.doFilter(request, response)
    }

}