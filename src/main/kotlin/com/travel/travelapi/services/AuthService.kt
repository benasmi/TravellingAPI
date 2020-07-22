package com.travel.travelapi.services

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.controllers.UserController
import com.travel.travelapi.models.Permission
import com.travel.travelapi.models.Role
import com.travel.travelapi.models.User
import com.travel.travelapi.models.UserProfile
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface AuthService {
    fun getUserByIdentifier(@Param("identifier") identifier: String, @Param("provider") provider: String?): User?
    fun getUserByRefreshToken(@Param("token") token: String): User?

    fun identifierExists(@Param("identifier") identifier: String): Boolean
    fun emailExists(@Param("email") email: String): Boolean
    fun createUser(@Param("user") user: User)

    fun getAllRoles(): ArrayList<Role>
    fun searchAdmin(keyword: String, roles: List<String>): List<UserProfile>

    fun getUserRoles(@Param("user") user: User): ArrayList<Role>
    fun getUserRolesByIdentifier(@Param("identifier") identifier: String): ArrayList<Role>
    fun getUserPermissions(@Param("roles") roles: ArrayList<Role>): ArrayList<Permission>

    fun getUserProfile(@Param("identifier") identifier: String, @Param("provider") provider: String): UserProfile?
    fun updateUser(@Param("user") user: User)

    fun updateInitialData(@Param("data") data: UserController.InitialData, @Param("userId") userId: Int)

    fun mapUserRoles(@Param("userId") userId: Int, @Param("roles") roles: List<Int>)

}