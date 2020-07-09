package com.travel.travelapi.jwt

data class JwtResponse(val access_token: String? = null,
                       val refresh_token: String?= null)