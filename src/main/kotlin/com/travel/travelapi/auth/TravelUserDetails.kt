package com.travel.travelapi.auth

import com.travel.travelapi.models.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User
import java.util.*
import kotlin.collections.ArrayList


data class TravelUserDetails(private val id: Long? = null,
                             private val identifier: String? = null,
                             val refreshToken: String? = null,
                             private val username: String?=null,
                             private val password: String?=null,
                             private val grantedAuthorities: List<GrantedAuthority>?= ArrayList()) : UserDetails, OAuth2User {

    private var attributes: Map<String, Any> = emptyMap()

    companion object{
        fun create(user: User): TravelUserDetails {
            return TravelUserDetails(
                    user.id,
                    user.identifier,
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

    override fun getAuthorities(): Collection<GrantedAuthority?> {
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
