package com.travel.travelapi.oauth2

import com.travel.travelapi.jwt.JwtConfig
import com.travel.travelapi.oauth2.HttpCookieOAuth2AuthorizationRequestRepository
import com.travel.travelapi.utils.CookieUtils
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.security.access.AuthorizationServiceException
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.io.IOException
import java.net.URI
import java.sql.Date
import java.time.LocalDate
import java.util.*
import javax.crypto.SecretKey
import javax.servlet.ServletException
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class OAuth2AuthenticationSuccessHandler(
        private val secretKey: SecretKey,
        private val jwtConfig: JwtConfig)
    : SimpleUrlAuthenticationSuccessHandler() {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        val targetUrl = determineTargetUrl(request, response, authentication)
//        val targetUrl = "http://localhost:8080/api/v1/oauth2/callback/google"
        if (response.isCommitted) {
            logger.debug("Response has already been committed. Unable to redirect to $targetUrl")
            return
        }

        clearAuthenticationAttributes(request)


//        response.addHeader(jwtConfig.authorizationHeader, jwtConfig.tokenPrefix + token)

        redirectStrategy.sendRedirect(request, response, targetUrl)
    }

    val REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri"

    override fun determineTargetUrl(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication): String? {
        val redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)

        if (redirectUri != null) {
            if (redirectUri.isPresent && !isAuthorizedRedirectUri(redirectUri.get())) {
                throw AuthorizationServiceException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication")
            }
        }

        val targetUrl: String = redirectUri!!.orElse(defaultTargetUrl)

        val token = Jwts.builder()
                .setSubject(authentication.name)
                .claim("authorities", authentication.authorities)
                .setIssuedAt(java.util.Date())
                .setExpiration(Date.valueOf(LocalDate.now().plusDays(jwtConfig.tokenExpirationAfterDays!!.toLong())))
                .signWith(secretKey)
                .compact()

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString()
    } //http://localhost:8080/api/v1/oauth2/authorize/google?redirect_uri=https://www.google.com/

    private fun isAuthorizedRedirectUri(uri: String): Boolean {
        return true
    }


}