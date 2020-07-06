package com.travel.travelapi.auth

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class TravelUserDetails(private val username: String?=null,
                             private val password: String?=null,
                             @JsonIgnore private val grantedAuthorities: Set<GrantedAuthority?>?= emptySet(),
                             @JsonIgnore private val isAccountNonExpired: Boolean?=true,
                             @JsonIgnore private val isAccountNonLocked: Boolean?=true,
                             @JsonIgnore private val isCredentialsNonExpired: Boolean?=true,
                             @JsonIgnore private val isEnabled: Boolean?=true) : UserDetails {

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
        return isAccountNonExpired!!
    }

    override fun isAccountNonLocked(): Boolean {
        return isAccountNonLocked!!
    }

    override fun isCredentialsNonExpired(): Boolean {
        return isCredentialsNonExpired!!
    }

    override fun isEnabled(): Boolean {
        return isEnabled!!
    }

}