package com.travel.travelapi.jwt

import org.springframework.security.core.GrantedAuthority

data class JwtResponse(val access_token: String? = null,
                       val refresh_token: String?= null,
                       val authorities: Collection<GrantedAuthority>?= ArrayList())