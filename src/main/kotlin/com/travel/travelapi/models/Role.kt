package com.travel.travelapi.models

data class Role(val roleId: Int? = null,
                val role: String? = null,
                val description: String? = null)


enum class Roles(val id: Int) {
    ROLE_ADMIN(1),
    ROLE_USER(2),
    ROLE_LIMBO(3)
}