package com.travel.travelapi.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import com.google.common.net.HttpHeaders;

@Component
@ConfigurationProperties(prefix = "application.jwt")
class JwtConfig {
    var secretKey: String? = null
    var tokenPrefix: String? = null
    var tokenExpirationAfterDays: Int? = null
    val authorizationHeader = "Authorization"

    enum class JwtTypes{
        ACCESS_TOKEN,
        EXCHANGE_TOKEN
    }
}