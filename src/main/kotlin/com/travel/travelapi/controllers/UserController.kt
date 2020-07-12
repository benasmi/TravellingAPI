package com.travel.travelapi.controllers

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.InvalidUserDataException
import com.travel.travelapi.models.UserProfile
import com.travel.travelapi.services.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(
        @Autowired private val authService: AuthService
){

    /**
     * Returns profile information of the currently logged in user
     */
    @RequestMapping("/info")
    fun userInfo() : UserProfile{
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails
        //Getting full profile from the database
        val user = authService.getUserProfile(principal.identifier!!, principal.provider!!) ?: throw InvalidUserDataException("User does not exist")
        //Fetching user roles
        user.roles = authService.getUserRolesByIdentifier(principal.identifier!!)
        //Getting authorities for the fetched roles
        user.permissions = authService.getUserPermissions(user.roles)

        return user
    }

}