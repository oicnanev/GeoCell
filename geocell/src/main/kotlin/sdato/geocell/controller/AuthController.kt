package sdato.geocell.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sdato.geocell.dto.request.LoginRequest
import sdato.geocell.model.UserSession
import sdato.geocell.repository.UserRepository
import sdato.geocell.repository.UserSessionRepository
import java.time.Instant
import java.util.UUID
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val userSessionRepository: UserSessionRepository,
) {
    @PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequest,
        request: HttpServletRequest,
    ): ResponseEntity<sdato.geocell.controller.LoginResponse?> {
        val authentication: Authentication =
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.username,
                    loginRequest.password,
                ),
            )

        SecurityContextHolder.getContext().authentication = authentication

        val user =
            userRepository.findByUsernameWithRolesAndGroups(loginRequest.username)
                ?: throw IllegalArgumentException("User not found")

        // Invalidar todas as sessões existentes para este usuário
        userSessionRepository.invalidateAllSessionsForUser(user.id)

        // Criar nova sessão
        val sessionToken = UUID.randomUUID().toString()
        val expiresAt = Instant.now().plusSeconds(TimeUnit.DAYS.toSeconds(7)) // 7 dias de expiração

        val userSession =
            UserSession(
                id = UUID.randomUUID().toString(),
                user = user,
                sessionToken = sessionToken,
                ipAddress = request.remoteAddr,
                userAgent = request.getHeader("User-Agent"),
                expiresAt = expiresAt,
            )

        userSessionRepository.save(userSession)

        val userRoles = user.roles.map { it.name }

        return ResponseEntity.ok(
            LoginResponse(
                sessionToken = sessionToken,
                expiresAt = expiresAt,
                userDetails =
                    UserDetailsResponse(
                        id = user.id,
                        username = user.username,
                        email = user.email,
                        roles = userRoles,
                        groups = user.groups.map { it.name },
                    ),
            ),
        )
    }

    @PostMapping("/logout")
    fun logout(): ResponseEntity<String> {
        return ResponseEntity.ok("Logout successful")
    }
}

data class LoginRequest(
    val username: String,
    val password: String,
)

data class LoginResponse(
    val sessionToken: String,
    val expiresAt: Instant,
    val userDetails: UserDetailsResponse,
)

data class UserDetailsResponse(
    val id: Long,
    val username: String,
    val email: String,
    val roles: List<String>,
    val groups: List<String>,
)
