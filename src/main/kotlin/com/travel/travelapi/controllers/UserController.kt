package com.travel.travelapi.controllers

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.InvalidUserDataException
import com.travel.travelapi.models.PlaceLocal
import com.travel.travelapi.models.Role
import com.travel.travelapi.models.UserProfile
import com.travel.travelapi.services.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
        @Autowired private val authService: AuthService
){

    /**
     * Returns profile information of the currently logged in user
     */
    @RequestMapping("/info")
    @PreAuthorize("hasAuthority('user:view')")
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

    /**
     * Returns all roles
     * Includes the roles that the current user is not attached to.
     */
    @GetMapping("/roles/all")
    @PreAuthorize("hasAuthority('role:read')")
    fun getAllRoles() : List<Role>{
        return authService.getAllRoles()
    }

    /**
     * A data class used to parse request body for setRoles endpoint
     */
    data class RoleUser(var roles: List<Int>,
                        var userId: Int)

    /**
     * Sets roles for user.
     */
    @PostMapping("/setRoles")
    @PreAuthorize("hasAuthority('role:manage')")
    fun setUserRoles(@RequestBody data: RoleUser){
        authService.mapUserRoles(data.userId, data.roles)
    }

    /**
     * User search functionality with advanced filtering options
     * @param keyword The keyword to filter users by
     * @param p Page number
     * @param s Page size
     * @param roles Roles to filter users by
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('user:search')")
    fun searchUsers(@RequestParam(required = true, defaultValue = "", name = "keyword") keyword: String,
                       @RequestParam(required = false, defaultValue = "1", name = "p") p: Int,
                       @RequestParam(required = false, defaultValue = "10", name = "s") s: Int,
                       @RequestParam(defaultValue = "", name = "roles") roles: String): PageInfo<UserProfile> {

        //Initializing paging
        PageHelper.startPage<UserProfile>(p, s)
        //Selecting and filtering users from mysql
        val users = authService.searchAdmin(keyword,
                if(roles.isNotEmpty()) roles.split(",") else ArrayList())

        //Mapping roles and permissions to each user
        users.forEach {
            it.roles = authService.getUserRolesByIdentifier(it.identifier!!)
//            it.permissions = authService.getUserPermissions(it.roles)
        }

        return PageInfo(users)
    }

}