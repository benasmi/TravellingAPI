package com.travel.travelapi.auth

import com.travel.travelapi.jwt.JwtConfig
import com.travel.travelapi.jwt.JwtTokenVerifier
import com.travel.travelapi.jwt.JwtUsernameAndPasswordAuthenticationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.crypto.SecretKey


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class ApplicationSecurityConfig(@Autowired private val authUserDetailsService: AuthUserDetailsService,
                                private val secretKey: SecretKey,
                                private val jwtConfig: JwtConfig) : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .csrf().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(getJWTAuthenticationFilter())
                .addFilterAfter(JwtTokenVerifier(secretKey, jwtConfig),JwtUsernameAndPasswordAuthenticationFilter::class.java)
                .authorizeRequests()
                .anyRequest()
                .authenticated()
    }

    /**
     * Inject password encoding technique into PasswordEncoder
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(10)
    }

    /**
     * Authentication manager that encodes given password and checks credentials against database
     */
    @Autowired
    @Throws(java.lang.Exception::class)
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth
                .userDetailsService<UserDetailsService>(authUserDetailsService)
                .passwordEncoder(passwordEncoder())
    }

    /**
     * Authentication filter
     */
    @Bean
    fun getJWTAuthenticationFilter(): JwtUsernameAndPasswordAuthenticationFilter {
        val filter = JwtUsernameAndPasswordAuthenticationFilter(jwtConfig,secretKey)
        filter.setAuthenticationManager(authenticationManager());
        filter.setFilterProcessesUrl("/auth/login")
        return filter
    }
}