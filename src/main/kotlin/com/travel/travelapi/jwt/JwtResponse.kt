package com.travel.travelapi.jwt

import org.springframework.security.core.GrantedAuthority

data class JwtResponse(var access_token: String? = null,
                       var refresh_token: String?= null,
                       var authorities: Collection<GrantedAuthority>?= ArrayList())