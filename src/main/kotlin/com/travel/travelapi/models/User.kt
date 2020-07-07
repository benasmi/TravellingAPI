package com.travel.travelapi.models

import com.travel.travelapi.oauth2.AuthProvider
import org.springframework.security.core.GrantedAuthority

data class User(
        var id: Long? = null,
        var name: String? = null,
        var email: String? = null,
        var identifier: String? = null,
        var imageUrl: String? = null,
        var emailVerified: Boolean = false,
        var password: String? = null,
        var provider: AuthProvider? = null,
        var refreshToken: String? = null,
        var phoneNumber: String? = null,
        var fk_locale: Int? = 1){

    var roles = ArrayList<Int>()
    var authorities :List<GrantedAuthority> = ArrayList()
}
