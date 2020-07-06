package com.travel.travelapi.oauth2.users


class GoogleOAuth2UserInfo(attributes: Map<String, Any>) : OAuth2UserInfo(attributes) {
    override val id: String?
        get() = attributes["sub"] as String?

    override val name: String?
        get() = attributes["name"] as String?

    override val email: String?
        get() = attributes["email"] as String?

    override val imageUrl: String?
        get() = attributes["picture"] as String?
}