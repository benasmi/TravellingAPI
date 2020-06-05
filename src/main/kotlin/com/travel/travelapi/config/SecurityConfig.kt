package com.travel.travelapi.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    /**
     * Use to create instance of [FirebaseAuthenticationTokenFilter].
     *
     * @return instance of [FirebaseAuthenticationTokenFilter]
     */
    @Throws(Exception::class)
    fun firebaseAuthenticationFilterBean(): FirebaseAuthenticationTokenFilter {
        return FirebaseAuthenticationTokenFilter()
    }

    @Throws(Exception::class)
    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/photo/view/**", "/placeApi/photo").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        // Custom security filter
        httpSecurity.addFilterBefore(firebaseAuthenticationFilterBean(),
                UsernamePasswordAuthenticationFilter::class.java)
    }
}