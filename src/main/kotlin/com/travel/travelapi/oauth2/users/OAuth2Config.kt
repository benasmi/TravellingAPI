package com.travel.travelapi.oauth2.users

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.util.*


@Component
@ConfigurationProperties(prefix = "app")
class OAuth2Config {
    private var authorizedRedirectUris: List<String> = ArrayList()
}