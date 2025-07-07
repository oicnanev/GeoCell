package sdato.geocell.repository

import sdato.geocell.model.User

interface UserRepositoryCustom {
    fun searchUsersWithFilters(
        username: String?,
        email: String?,
        isActive: Boolean?,
        groupIds: List<Long>?,
    ): List<User>
}
