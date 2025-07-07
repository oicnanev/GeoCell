package sdato.geocell.dto.response

import java.time.Instant

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
)
