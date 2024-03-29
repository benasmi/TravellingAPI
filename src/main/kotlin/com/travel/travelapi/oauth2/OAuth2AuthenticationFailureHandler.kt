package com.travel.travelapi.oauth2

import com.travel.travelapi.oauth2.HttpCookieOAuth2AuthorizationRequestRepository
import com.travel.travelapi.utils.CookieUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class OAuth2AuthenticationFailureHandler(val httpCookieOAuth2AuthorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository) : SimpleUrlAuthenticationFailureHandler() {
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(request: HttpServletRequest, response: HttpServletResponse, exception: AuthenticationException) {
        var targetUrl = CookieUtils.getCookie(request, httpCookieOAuth2AuthorizationRequestRepository?.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map { obj: Cookie -> obj.value }
                .orElse("/")
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl!!)
                .queryParam("error", exception.localizedMessage)
                .build().toUriString()
        httpCookieOAuth2AuthorizationRequestRepository!!.removeAuthorizationRequestCookies(request, response)
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }
}