package com.travel.travelapi.auth

import com.travel.travelapi.models.User
import com.travel.travelapi.oauth2.AuthProvider
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.reactive.result.SimpleHandlerAdapter
import java.util.*
import kotlin.collections.ArrayList


data class TravelUserDetails(public var id: Long? ,
                             var identifier: String? ,
                             var provider: String? ,
                             var refreshToken: String? ,
                             private var username: String?,
                             private var password: String?,
                             public var grantedAuthorities: List<GrantedAuthority>?= ArrayList()) : UserDetails, OAuth2User {

    private var attributes: Map<String, Any> = emptyMap()
    var device: String? = ""

    companion object{
        fun create(user: User): TravelUserDetails {
            return TravelUserDetails(
                    user.id,
                    user.identifier,
                    user.provider?.name,
                    user.refreshToken,
                    user.email,
                    user.password,
                    user.authorities

            )
        }

        fun create(user: User, attributes: Map<String, Any>): TravelUserDetails {
            val userPrincipal: TravelUserDetails = create(user)
            userPrincipal.attributes = attributes
            return userPrincipal
        }

        fun createGrantedAuthorities(roles: List<String>): List<GrantedAuthority>{
            val authorities = ArrayList<GrantedAuthority>()
            for (role in roles) {
                authorities.add(SimpleGrantedAuthority(role))
            }
            return authorities
        }
    }

    /**
     * Returns a boolean, that indicates whether the user has a given authority.
     * It checks for roles if authority name begins with ROLE_
     */
    fun hasAuthority(authority: SimpleGrantedAuthority): Boolean{
        return authorities.contains(authority)
    }

    /**
     * Returns a boolean, that indicates whether the user has a given authority.
     * It checks for roles if authority name begins with ROLE_
     * This override method creates a SimpleGrantedAuthority from the given auhority string
     */
    fun hasAuthority(authority: String): Boolean{
        return hasAuthority(SimpleGrantedAuthority(authority))
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return grantedAuthorities!!
    }

    override fun getPassword(): String {
        return password!!
    }

    override fun getUsername(): String {
        return username!!
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getName(): String {
       return identifier!!
    }

    override fun getAttributes(): Map<String, Any> {
       return attributes
    }


}
