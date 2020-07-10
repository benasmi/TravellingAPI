package com.travel.travelapi.services

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.models.Permission
import com.travel.travelapi.models.Role
import com.travel.travelapi.models.User
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface AuthService {
    fun getUserByIdentifier(@Param("identifier") identifier: String, @Param("provider") provider: String?): User?
    fun identifierExists(@Param("identifier") identifier: String): Boolean
    fun emailExists(@Param("email") email: String): Boolean
    fun createUser(@Param("user") user: User)

    fun getUserRoles(@Param("user") user: User): ArrayList<Role>
    fun getUserPermissions(@Param("roles") roles: ArrayList<Role>): ArrayList<Permission>

    fun updateUser(@Param("user") user: User)
    fun mapUserRoles(@Param("user") user: User)

}