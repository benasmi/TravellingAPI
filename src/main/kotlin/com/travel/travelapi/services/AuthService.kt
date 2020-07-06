package com.travel.travelapi.services

import com.travel.travelapi.auth.TravelUserDetails
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface AuthService {
    fun getUserByUsername(@Param("username") username: String): TravelUserDetails
}