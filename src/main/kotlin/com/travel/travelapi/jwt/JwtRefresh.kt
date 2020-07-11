package com.travel.travelapi.jwt

data class JwtRefresh(val identifier: String? = null,
                      val refreshToken: String? = null,
                      val provider: String? = null) {

}