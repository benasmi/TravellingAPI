package com.travel.travelapi.controllers

import com.travel.travelapi.auth.AuthUserDetailsService
import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.jwt.JwtConfig
import com.travel.travelapi.jwt.JwtRequest
import com.travel.travelapi.models.Permission
import com.travel.travelapi.models.Role
import com.travel.travelapi.models.Roles
import com.travel.travelapi.models.User
import com.travel.travelapi.services.AuthService
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.sql.Date
import java.time.LocalDate
import java.util.*
import javax.crypto.SecretKey
import javax.naming.AuthenticationException

@RestController("/auth")
class AuthController(@Autowired private val authService: AuthService) {

    /**
     * Gets user row by identifier
     * @param identifier
     * @return TravelUserDetails
     */
    fun getUserByIdentifier(@RequestParam identifier: String): TravelUserDetails{
            val user = authService.getUserByIdentifier(identifier)

            val roles = getUserRoles(user)
            val permissions = getUserPermissions(roles)
            val grantedAuthorities = ArrayList<String>()

            roles.forEach{
                grantedAuthorities.add(it.role!!)
            }
            permissions.forEach{
                grantedAuthorities.add(it.permission!!)
            }


            return TravelUserDetails(user.id,
                    user.identifier,
                    user.password,TravelUserDetails.createGrantedAuthorities(grantedAuthorities))
    }

    fun identifierExists(identifier: String): Boolean{
        return authService.identifierExists(identifier).isNotEmpty()
    }


    /**
     * Creates new user
     * @param user
     */
    @Throws(AuthenticationException::class)
    @PostMapping("/registration")
    fun createUser(@RequestBody user: User){
        if(identifierExists(user.email!!)) throw AuthenticationException("Email already exists")

        user.refreshToken = UUID.randomUUID().toString()
        user.roles.add(Roles.ROLE_USER.i)
        authService.createUser(user)

        mapUserRoles(user)
    }

    /**
     * Get mapped user roles
     * @param user
     * @return user roles
     */
    fun getUserRoles(user: User): ArrayList<Role>{
        return authService.getUserRoles(user)
    }

    /**
     * Get mapped user permissions
     * @param user
     * @return user permissions
     */
    fun getUserPermissions(roles: ArrayList<Role>): ArrayList<Permission>{
        return authService.getUserPermissions(roles)
    }

    fun mapUserRoles(user: User){
        authService.
    }

//    @PostMapping("/login")
//    fun generateJwtToken(@RequestBody jwtRequest: JwtRequest): String{
//
//        val auth = authenticate(jwtRequest.username, jwtRequest.password)
//
//        val token = Jwts.builder()
//                .setSubject(auth.name)
//                .claim("authorities", auth.authorities)
//                .setIssuedAt(java.util.Date())
//                .setExpiration(Date.valueOf(LocalDate.now().plusDays(jwtConfig.tokenExpirationAfterDays!!.toLong())))
//                .signWith(secretKey)
//                .compact()
//
//        return token
//    }
//
//    fun authenticate(username: String, password: String): Authentication {
//        try {
//           return authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
//        } catch (e: DisabledException) {
//            throw Exception("USER_DISABLED", e)
//        } catch (e: BadCredentialsException) {
//            throw Exception("INVALID_CREDENTIALS", e)
//        }
//    }

}