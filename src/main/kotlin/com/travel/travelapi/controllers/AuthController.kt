package com.travel.travelapi.controllers

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.InvalidUserDataException
import com.travel.travelapi.jwt.JwtConfig
import com.travel.travelapi.jwt.JwtRefresh
import com.travel.travelapi.jwt.JwtRequest
import com.travel.travelapi.jwt.JwtResponse
import com.travel.travelapi.models.Permission
import com.travel.travelapi.models.Role
import com.travel.travelapi.models.Roles
import com.travel.travelapi.models.User
import com.travel.travelapi.oauth2.AuthProvider
import com.travel.travelapi.services.AuthService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.sql.Date
import java.time.LocalDate
import java.util.*
import java.util.stream.Collectors
import javax.crypto.SecretKey
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.naming.AuthenticationException
import javax.validation.Valid


@RestController
@RequestMapping("/auth")
class AuthController(@Autowired private val authService: AuthService,
                     @Lazy private val passwordEncoder: PasswordEncoder,
                     @Lazy private val authenticationManager: AuthenticationManager,
                     @Lazy private val secretKey: SecretKey,
                     @Lazy private val jwtConfig: JwtConfig) {

    /**
     * Creates new user
     * @param user
     */
    @Throws(AuthenticationException::class,InvalidUserDataException::class)
    @PostMapping("/registration")
    fun createUser(@Valid @RequestBody user: User, bindingResult: BindingResult){
        if(bindingResult.hasErrors()){
            throw InvalidUserDataException("Password is to weak")
        }
        validUserData(user)

        val formedUser = User()
        formedUser.refreshToken = UUID.randomUUID().toString()
        formedUser.password = passwordEncoder.encode(user.password)
        formedUser.identifier = user.email
        formedUser.email = user.email
        formedUser.birthday = user.birthday
        formedUser.provider = AuthProvider.local

        formedUser.roles.add(Roles.ROLE_USER.id)
        authService.createUser(formedUser)

    }

    fun identifierExists(identifier: String): Boolean{
        return authService.identifierExists(identifier)
    }

    fun emailExists(email: String): Boolean{
        return authService.emailExists(email)
    }

    @Throws(InvalidUserDataException::class)
    fun validUserData(user : User){
        if(identifierExists(user.identifier?:"")) throw InvalidUserDataException("Identifier already exists")
        if(emailExists(user.email!!)) throw InvalidUserDataException("Email already exists")
        isValidEmailAddress(user.email)
        isValidBirthday(user.birthday?:"")
    }

    @Throws(InvalidUserDataException::class)
    fun isValidEmailAddress(email: String?) {
        try {
            val emailAddr = InternetAddress(email)
            emailAddr.validate()
        } catch (ex: AddressException) {
            throw  InvalidUserDataException("Invalid email address")
        }
    }

    @Throws(InvalidUserDataException::class)
    fun isValidBirthday(birthday: String) {
        try {
            val date = SimpleDateFormat("dd/MM/yyyy").parse(birthday)
            if(date.after(Date())){
                throw InvalidUserDataException("Only users that are already born can register!")
            }
        } catch (ex: ParseException) {
            throw  InvalidUserDataException("Invalid date format!")
        }
    }

    /**
     * Authenticates user by username and password
     * @return JwtResponse
     */
    @PostMapping("/login")
    fun generateJwtToken(@RequestBody jwtRequest: JwtRequest): JwtResponse {

        val auth = authenticate(jwtRequest.identifier!!, jwtRequest.password!!)
        val userDetails = auth.principal as TravelUserDetails

        val token = Jwts.builder()
                .setSubject(auth.name)
                .claim("authorities", auth.authorities)
                .setIssuedAt(java.util.Date())
                .setExpiration(Date.valueOf(LocalDate.now().plusDays(jwtConfig.tokenExpirationAfterDays!!.toLong())))
                .signWith(secretKey)
                .compact()

        return JwtResponse(token,userDetails.refreshToken)
    }

    fun authenticate(username: String, password: String): Authentication {
        try {
            return authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        } catch (e: DisabledException) {
            throw Exception("USER_DISABLED", e)
        } catch (e: BadCredentialsException) {
            throw Exception("INVALID_CREDENTIALS", e)
        }
    }


    @PostMapping("/refresh")
    @Throws(InvalidUserDataException::class)
    fun refreshAccessToken(@RequestBody jwtRefresh: JwtRefresh): String{
        val user = getUserByIdentifier(jwtRefresh.identifier!!)?: throw InvalidUserDataException("Invalid refresh token!")
        if(user.refreshToken == jwtRefresh.refreshToken){
            val token = Jwts.builder()
                    .setSubject(jwtRefresh.identifier)
                    .claim("authorities", user.authorities)
                    .setIssuedAt(java.util.Date())
                    .setExpiration(Date.valueOf(LocalDate.now().plusDays(jwtConfig.tokenExpirationAfterDays!!.toLong())))
                    .signWith(secretKey)
                    .compact()
            return token
        }else{
            throw InvalidUserDataException("Invalid refresh token!")
        }
    }



    /**
     * Gets user row by identifier
     * @param identifier
     * @return TravelUserDetails
     */
    fun getUserByIdentifier(@RequestParam identifier: String): User?{
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

        user.authorities = TravelUserDetails.createGrantedAuthorities(grantedAuthorities)
        return user

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



    /**
     * Creates new user OAuth2
     * @param user
     */
    @Throws(AuthenticationException::class)
    fun createUserOauth2(@RequestBody user: User){
        if(identifierExists(user.email!!)) throw AuthenticationException("Email already exists")

        user.refreshToken = UUID.randomUUID().toString()
        user.password = passwordEncoder.encode(user.password)

        user.roles.add(Roles.ROLE_USER.id)
        authService.createUser(user)
    }








}