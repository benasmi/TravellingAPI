package com.travel.travelapi.models

data class Role(val roleId: Int? = null,
                val role: String? = null,
                val description: String? = null)


enum class Roles(val i: Int) {
    ROLE_ADMIN(1),
    ROLE_USER(2)
}