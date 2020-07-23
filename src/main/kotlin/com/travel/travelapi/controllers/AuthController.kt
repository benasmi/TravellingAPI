package com.travel.travelapi.controllers

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.FailedApiRequestException
import com.travel.travelapi.exceptions.InvalidUserDataException
import com.travel.travelapi.jwt.JwtConfig
import com.travel.travelapi.jwt.JwtRefresh
import com.travel.travelapi.jwt.JwtRequest
import com.travel.travelapi.jwt.JwtResponse
import com.travel.travelapi.models.*
import com.travel.travelapi.oauth2.AuthProvider
import com.travel.travelapi.services.AuthService
import com.travel.travelapi.services.PhotoService
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.sql.Date
import java.time.LocalDate
import java.util.*
import javax.crypto.SecretKey
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.naming.AuthenticationException
import javax.validation.Valid


@RestController
@RequestMapping("/auth")
class AuthController(@Autowired private val authService: AuthService,
                     @Autowired private val photoService: PhotoService,
                     @Lazy private val passwordEncoder: PasswordEncoder,
                     @Lazy private val authenticationManager: AuthenticationManager,
                     @Lazy private val secretKey: SecretKey,
                     @Lazy private val jwtConfig: JwtConfig) {

    /**
     * Creates new user
     * @param user
     */
    @PostMapping("/registration")
    fun createUser(@Valid @RequestBody user: User, bindingResult: BindingResult): Long{
        if(bindingResult.hasErrors()){
            throw InvalidUserDataException("Password is to weak")
        }

        validUserData(user)

        val formedUser = User()
        formedUser.name = user.name
        formedUser.surname = user.surname
        formedUser.refreshToken = UUID.randomUUID().toString()
        formedUser.password = passwordEncoder.encode(user.password)
        formedUser.identifier = user.email
        formedUser.email = user.email
        formedUser.birthday = user.birthday
        formedUser.provider = AuthProvider.local
        formedUser.roles.add(Roles.ROLE_LIMBO.id)

        if(!user.imageUrl.isNullOrEmpty()){
            val photo = Photo(null, user.imageUrl, null)
            photoService.insertPhoto(photo)
            formedUser.fk_photoId = photo.photoId
        }
        authService.createUser(formedUser)

        return formedUser.id!!
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
        if(birthday.isNullOrEmpty()){
            return
        }
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
                .claim("provider", "local")
                .claim("type", JwtConfig.JwtTypes.ACCESS_TOKEN.name)
                .setIssuedAt(Date())
                .setExpiration(Date.valueOf(LocalDate.now().plusDays(jwtConfig.tokenExpirationAfterDays!!.toLong())))
                .signWith(secretKey)
                .compact()

        return JwtResponse(token,userDetails.refreshToken, userDetails.authorities)
    }

    fun authenticate(identifier: String, password: String): Authentication {
        try {
            val auth = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(identifier, password))

            getUserByIdentifier(identifier, AuthProvider.local.name)
            SecurityContextHolder.getContext().authentication = auth
            return auth
        } catch (e: DisabledException) {
            throw InvalidUserDataException("User is currently disabled")
        } catch (e: BadCredentialsException) {
            throw InvalidUserDataException("Wrong username or password")
        }
    }


    @PostMapping("/refresh")
    @Throws(InvalidUserDataException::class)
    fun refreshAccessToken(@RequestBody refreshToken: String): JwtResponse{
        val user = authService.getUserByRefreshToken(refreshToken)?: throw InvalidUserDataException("User does not exist!")
        if(user.refreshToken == refreshToken){
            val token = Jwts.builder()
                    .setSubject(user.identifier)
                    .claim("provider", user.provider)
                    .claim("type", JwtConfig.JwtTypes.ACCESS_TOKEN.name)
                    .setIssuedAt(Date())
                    .setExpiration(Date.valueOf(LocalDate.now().plusDays(jwtConfig.tokenExpirationAfterDays!!.toLong())))
                    .signWith(secretKey)
                    .compact()
            return JwtResponse(token,null,user.authorities)
        }else{
            throw InvalidUserDataException("Invalid refresh token!")
        }
    }



    /**
     * Gets user row by identifier
     * @param identifier
     * @return TravelUserDetails
     */
    fun getUserByIdentifier(identifier: String, provider: String? = null): User?{
        val user = authService.getUserByIdentifier(identifier, provider)?: return null

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
    fun createUserOauth2(user: User){
        user.roles.add(Roles.ROLE_LIMBO.id)
        authService.createUser(user)
    }

    /**
     * Updates existing OAuth2 user
     */
    fun updateUserOauth2(user: User){
        if(user.fk_photoId != null){
            photoService.updatePhoto(user.fk_photoId!!,user.imageUrl!!)
        }else{
            val photo = Photo(null, user.imageUrl,null)
            photoService.insertPhoto(photo)
            user.fk_photoId = photo.photoId
        }
        authService.updateUser(user)
    }

    /**
     * Exchanges short lived JWT OAuth2 token for long lived JWT token and refresh token
     * @param token short lived JWT token
     */
    @GetMapping("/exchange")
    fun exchangeTokens(@RequestParam(name="token") token: String): JwtResponse{
        try {
            val claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            val body = claimsJws.body
            val identifier = body.subject
            val provider = body["provider"] as String
            val type = body["type"] as String

            if(type != JwtConfig.JwtTypes.EXCHANGE_TOKEN.name){
                throw InvalidUserDataException(String.format("Token %s is not type of EXCHANGE_TOKEN", token))
            }

            val user = getUserByIdentifier(identifier, provider)

            val refresh = refreshAccessToken(user?.refreshToken!!)
            refresh.refresh_token = user.refreshToken
            return refresh
        } catch (e: JwtException) {
            throw InvalidUserDataException(String.format("Token %s cannot be trusted", token))
        }
    }

    fun getPermissionsByIdentifier(identifier: String): List<GrantedAuthority>{
        val roles = authService.getUserRolesByIdentifier(identifier)
        val permissions = getUserPermissions(roles)
        val grantedAuthorities = ArrayList<String>()

        roles.forEach{
            grantedAuthorities.add(it.role!!)
        }
        permissions.forEach{
            grantedAuthorities.add(it.permission!!)
        }
        return TravelUserDetails.createGrantedAuthorities(grantedAuthorities)
    }
}