package com.travel.travelapi.models

import com.travel.travelapi.oauth2.AuthProvider

data class User(
        var id: Long? = null,
        var name: String? = null,
        var email: String? = null,
        var imageUrl: String? = null,
        var emailVerified: Boolean = false,
        var password: String? = null,
        var provider: AuthProvider? = null,
        var providerId: String? = null)
