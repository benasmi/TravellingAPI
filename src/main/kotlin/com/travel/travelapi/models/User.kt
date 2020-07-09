package com.travel.travelapi.models

import com.travel.travelapi.oauth2.AuthProvider
import com.travel.travelapi.validation.ValidPassword
import org.hibernate.validator.constraints.Length
import org.springframework.security.core.GrantedAuthority

data class User(
        var id: Long? = null,
        var name: String? = null,
        var email: String? = null,
        var identifier: String? = null,
        var imageUrl: String? = null,
        var emailVerified: Boolean = false,
        @ValidPassword var password: String? = null,
        var provider: AuthProvider? = null,
        var refreshToken: String? = null,
        var fk_locale: Int? = 1,
        var birthday: String? = null){

    var roles = ArrayList<Int>()
    var authorities :List<GrantedAuthority> = ArrayList()
}
