package com.travel.travelapi.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "application.jwt")
class JwtConfig {
    var secretKey: String? = null
    var tokenPrefix: String? = null
    var tokenExpirationAfterDays: Int? = null
    val authorizationHeader = "Authorization"
}