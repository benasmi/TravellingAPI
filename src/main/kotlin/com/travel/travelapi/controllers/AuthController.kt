package com.travel.travelapi.controllers

import com.travel.travelapi.auth.AuthUserDetailsService
import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.jwt.JwtConfig
import com.travel.travelapi.jwt.JwtRequest
import com.travel.travelapi.models.User
import com.travel.travelapi.services.AuthService
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.sql.Date
import java.time.LocalDate
import javax.crypto.SecretKey

@RestController
class AuthController(@Autowired private val authService: AuthService) {

    fun getUserByUserName(username: String): TravelUserDetails{
            return authService.getUserByUsername(username)
    }

    fun findByEmail(email: String) : User?{
        return authService.getUserByEmail(email)
    }

    fun emailExists(email: String): Boolean{
        return authService.emailExists(email).isNotEmpty()
    }


//    @PostMapping("/login")
//    fun generateJwtToken(@RequestBody jwtRequest: JwtRequest): String{
//
//        val auth = authenticate(jwtRequest.username, jwtRequest.password)
//
//        val token = Jwts.builder()
//                .setSubject(auth.name)
//                .claim("authorities", auth.authorities)
//                .setIssuedAt(java.util.Date())
//                .setExpiration(Date.valueOf(LocalDate.now().plusDays(jwtConfig.tokenExpirationAfterDays!!.toLong())))
//                .signWith(secretKey)
//                .compact()
//
//        return token
//    }
//
//    fun authenticate(username: String, password: String): Authentication {
//        try {
//           return authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
//        } catch (e: DisabledException) {
//            throw Exception("USER_DISABLED", e)
//        } catch (e: BadCredentialsException) {
//            throw Exception("INVALID_CREDENTIALS", e)
//        }
//    }

}