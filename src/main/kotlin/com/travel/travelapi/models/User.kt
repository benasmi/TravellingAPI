package com.travel.travelapi.models

import com.travel.travelapi.oauth2.AuthProvider
import com.travel.travelapi.validation.ValidPassword
import org.hibernate.validator.constraints.Length
import org.springframework.security.core.GrantedAuthority

data class User(
        var id: Long? = null,
        var name: String? = null,
        var surname: String? = null,
        var email: String? = null,
        var identifier: String? = null,
        var imageUrl: String? = null,
        var emailVerified: Boolean = false,
        @ValidPassword var password: String? = null,
        var provider: AuthProvider? = null,
        var refreshToken: String? = null,
        var fk_locale: Int? = 1,
        var fk_photoId: Int? = null,
        var birthday: String? = null){

    var roles = ArrayList<Int>()
    var authorities :List<GrantedAuthority> = ArrayList()
}

data class UserProfile(val name: String? = null,
                       val identifier: String? = null,
                       val id: Long? = null,
                       val surname: String? = null,
                       val email: String? = null,
                       val provider: String? = null,
                       val phoneNumber: String? = null,
                       val birthday: String? = null,
                       val gender: String? = null,
                       val imageUrl: String? = null,
                       var roles: ArrayList<Role> = ArrayList(),
                       var permissions: ArrayList<Permission> = ArrayList())
