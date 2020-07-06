package com.travel.travelapi.jwt

data class JwtRequest(val username:String?=null,
                      val password:String?=null) {
}