package com.travel.travelapi.auth

import com.travel.travelapi.controllers.AuthController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class AuthUserDetailsService(@Autowired @Lazy private val authController: AuthController) : UserDetailsService{
    override fun loadUserByUsername(username: String): UserDetails {
        return authController.getUserByUserName(username) ?: throw UsernameNotFoundException("Username not found")
    }
}