package com.travel.travelapi.models

import com.travel.travelapi.oauth2.AuthProvider

data class User(
        val id: Long? = null,
        val name: String? = null,
        val email: String? = null,
        val imageUrl: String? = null,
        val emailVerified: Boolean = false,
        val password: String? = null,
        val provider: AuthProvider? = null,
        val providerId: String? = null)
