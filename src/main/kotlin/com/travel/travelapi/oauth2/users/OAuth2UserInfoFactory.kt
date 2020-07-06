package com.travel.travelapi.oauth2.users

import com.travel.travelapi.oauth2.AuthProvider
import javax.naming.AuthenticationException


object OAuth2UserInfoFactory {
    fun getOAuth2UserInfo(registrationId: String, attributes: Map<String, Any>): OAuth2UserInfo {
        return when {
            registrationId.equals(AuthProvider.google.toString(), ignoreCase = true) -> {
                GoogleOAuth2UserInfo(attributes)
            }
            registrationId.equals(AuthProvider.facebook.toString(), ignoreCase = true) -> {
                FacebookOAuth2UserInfo(attributes)
            }
            else -> {
                throw AuthenticationException("Sorry! Login with $registrationId is not supported yet.")
            }
        }
    }
}