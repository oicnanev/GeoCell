package sdato.geocell.dto.response

import java.time.Instant

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val isActive: Boolean,
    val groupIds: List<Long>,
    val roleIds: List<Long>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val lastLogin: Instant?,
)
