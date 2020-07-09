package com.travel.travelapi.jwt

data class JwtRequest(val identifier:String?=null,
                      val password:String?=null) {
}