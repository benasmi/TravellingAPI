package com.travel.travelapi.oauth2

import com.travel.travelapi.utils.CookieUtils
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class HttpCookieOAuth2AuthorizationRequestRepository : AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    private val OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request"
    val REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri"
    private val cookieExpireSeconds = 180

    override fun loadAuthorizationRequest(request: HttpServletRequest?): OAuth2AuthorizationRequest {
        return CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map { cookie: Cookie? -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest::class.java) }
                .orElse(null)
    }

    override fun removeAuthorizationRequest(request: HttpServletRequest?): OAuth2AuthorizationRequest {
        return this.loadAuthorizationRequest(request)
    }

    override fun saveAuthorizationRequest(authorizationRequest: OAuth2AuthorizationRequest?, request: HttpServletRequest?, response: HttpServletResponse?) {
        if (authorizationRequest == null) {
            CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
            CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME)
            return
        }

        CookieUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtils.serialize(authorizationRequest), cookieExpireSeconds)
        val redirectUriAfterLogin = request!!.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME)
        if (redirectUriAfterLogin.isNotEmpty()) {
            CookieUtils.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, cookieExpireSeconds)
        }
    }

    fun removeAuthorizationRequestCookies(request: HttpServletRequest?, response: HttpServletResponse?) {
        CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME)
    }
}