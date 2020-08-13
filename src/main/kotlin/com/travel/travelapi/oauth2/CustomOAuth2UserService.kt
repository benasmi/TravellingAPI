package com.travel.travelapi.oauth2

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.controllers.AuthController
import com.travel.travelapi.models.Roles
import com.travel.travelapi.models.User
import com.travel.travelapi.oauth2.users.OAuth2UserInfo
import com.travel.travelapi.oauth2.users.OAuth2UserInfoFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Service
import java.util.*
import javax.naming.AuthenticationException


@Service
class CustomOAuth2UserService(@Autowired private val authController: AuthController): DefaultOAuth2UserService() {

    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest?): OAuth2User {
        val oAuth2User = super.loadUser(oAuth2UserRequest)

        return try {
            processOAuth2User(oAuth2UserRequest!!, oAuth2User)!!
        } catch (ex: AuthenticationException) {
            throw ex
        } catch (ex: Exception) { // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw InternalAuthenticationServiceException(ex.message, ex.cause)
        }
    }

    private fun processOAuth2User(oAuth2UserRequest: OAuth2UserRequest, oAuth2User: OAuth2User): OAuth2User? {
        val oAuth2UserInfo: OAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.clientRegistration.registrationId, oAuth2User.attributes)
        if (oAuth2UserInfo.email.isNullOrEmpty()) {
            throw AuthenticationException("Email not found from OAuth2 provider")
        }

        val user = authController.getUserByIdentifier(oAuth2UserInfo.id!!, AuthProvider.valueOf(oAuth2UserRequest.clientRegistration.registrationId).name)

        val formedUser = if (user != null) {
            if (user.provider!! != AuthProvider.valueOf(oAuth2UserRequest.clientRegistration.registrationId)) {
                throw AuthenticationException("Looks like you're signed up with " +
                        user.provider.toString() + " account. Please use your " + user.provider.toString() +
                        " account to login.")
            }
            //Currently does not do anything, because profile is controlled within Travelgo App
            updateExistingUser(user, oAuth2UserInfo)
        } else {
            registerNewUser(oAuth2UserRequest, oAuth2UserInfo)
        }

        return TravelUserDetails.create(formedUser, oAuth2User.attributes)
    }

    private fun registerNewUser(oAuth2UserRequest: OAuth2UserRequest, oAuth2UserInfo: OAuth2UserInfo): User {

        val user = User(
                null,
                oAuth2UserInfo.name,
                null,
                oAuth2UserInfo.email,
                oAuth2UserInfo.id,
                oAuth2UserInfo.imageUrl,
                true,
                null,
                AuthProvider.valueOf(oAuth2UserRequest.clientRegistration.registrationId),
                UUID.randomUUID().toString(),
                null,
                null,
                "20/08/1999"
        )
        authController.createUserOauth2(user)
        return user
    }

    private fun updateExistingUser(existingUser: User, oAuth2UserInfo: OAuth2UserInfo): User {
//        existingUser.name = oAuth2UserInfo.name
//        existingUser.imageUrl = oAuth2UserInfo.imageUrl
//        authController.updateUserOauth2(existingUser)
        return existingUser
    }
}