package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.User

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}
