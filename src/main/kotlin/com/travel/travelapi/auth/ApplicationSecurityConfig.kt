package com.travel.travelapi.auth

import com.travel.travelapi.jwt.JwtConfig
import com.travel.travelapi.jwt.JwtTokenVerifier
import com.travel.travelapi.jwt.JwtUsernameAndPasswordAuthenticationFilter
import com.travel.travelapi.oauth2.CustomOAuth2UserService
import com.travel.travelapi.oauth2.HttpCookieOAuth2AuthorizationRequestRepository
import com.travel.travelapi.oauth2.OAuth2AuthenticationFailureHandler
import com.travel.travelapi.oauth2.OAuth2AuthenticationSuccessHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import javax.crypto.SecretKey


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
class ApplicationSecurityConfig(@Autowired private val authUserDetailsService: AuthUserDetailsService,
                                private val secretKey: SecretKey,
                                private val jwtConfig: JwtConfig,
                                @Autowired private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
                                @Autowired @Lazy private val oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler,
                                @Autowired private val customOAuth2UserService: CustomOAuth2UserService) : WebSecurityConfigurerAdapter() {


    @Bean
    fun cookieAuthorizationRequestRepository(): HttpCookieOAuth2AuthorizationRequestRepository {
        return HttpCookieOAuth2AuthorizationRequestRepository()
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .csrf().disable()
                .cors()
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .addFilter(getJWTAuthenticationFilter())
                .addFilterAfter(JwtTokenVerifier(secretKey, jwtConfig), JwtUsernameAndPasswordAuthenticationFilter::class.java)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .oauth2Login()
                    .authorizationEndpoint()
                        .baseUri("/oauth2/authorize")
                        .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                        .and()
                    .redirectionEndpoint()
                        .baseUri("/oauth2/callback/*")
                        .and()
                    .userInfoEndpoint()
                        .userService(customOAuth2UserService)
                        .and()
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler);
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