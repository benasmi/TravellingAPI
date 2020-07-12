package com.travel.travelapi.auth

import com.travel.travelapi.controllers.AuthController
import com.travel.travelapi.jwt.JwtConfig
import com.travel.travelapi.jwt.JwtTokenVerifier
import com.travel.travelapi.oauth2.CustomOAuth2UserService
import com.travel.travelapi.oauth2.HttpCookieOAuth2AuthorizationRequestRepository
import com.travel.travelapi.oauth2.OAuth2AuthenticationFailureHandler
import com.travel.travelapi.oauth2.OAuth2AuthenticationSuccessHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
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
@EnableGlobalMethodSecurity(prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
class ApplicationSecurityConfig(@Autowired @Lazy private val authUserDetailsService: AuthUserDetailsService,
                                @Autowired @Lazy private val authController: AuthController,
                                private val secretKey: SecretKey,
                                private val jwtConfig: JwtConfig,
                                @Autowired @Lazy private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
                                @Autowired @Lazy private val oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler,
                                @Autowired @Lazy private val customOAuth2UserService: CustomOAuth2UserService) : WebSecurityConfigurerAdapter() {


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
                .exceptionHandling()
                .authenticationEntryPoint(RestAuthenticationEntryPoint())
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterAfter(JwtTokenVerifier(secretKey, jwtConfig, authController), UsernamePasswordAuthenticationFilter::class.java)
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

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers("/auth/**")
        web.ignoring().antMatchers("/oauth2/**")
    }

    /**
     * Inject password encoding technique into PasswordEncoder
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(10)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
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

}