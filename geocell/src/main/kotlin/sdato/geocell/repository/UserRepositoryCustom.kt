package sdato.geocell.repository

import org.springframework.stereotype.Repository
import sdato.geocell.model.User

@Repository
interface UserRepositoryCustom {
    fun searchUsersWithFilters(
        username: String?,
        email: String?,
        isActive: Boolean?,
        groupIds: List<Long>?,
    ): List<User>
}
