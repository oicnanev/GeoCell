package sdato.geocell.domain.entities

data class AuthenticatedUser(
    val user: User,
    val token: String,
)
