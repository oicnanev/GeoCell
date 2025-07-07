package sdato.geocell.dto.request

data class UserSearchFilter(
    val username: String? = null,
    val email: String? = null,
    val isActive: Boolean? = null,
    val groupIds: List<Long>? = null,
)
