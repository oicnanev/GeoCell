package sdato.geocell.domain.entities

import kotlinx.datetime.Instant

data class Token(
    val tokenValidationInfo: TokenValidationInfo,
    val userId: Int,
    val createdAt: Instant,
    val lastUsedAt: Instant,
)
