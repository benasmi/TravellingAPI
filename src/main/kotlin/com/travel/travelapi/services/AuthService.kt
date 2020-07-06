package com.travel.travelapi.services

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.models.User
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface AuthService {
    fun getUserByUsername(@Param("username") username: String): TravelUserDetails

    fun getUserByEmail(@Param("email") email: String): User
    fun emailExists(@Param("email") email: String): String
}