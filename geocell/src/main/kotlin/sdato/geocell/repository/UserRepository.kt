package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import sdato.geocell.model.User

@Repository
interface UserRepository : JpaRepository<User, Long>, UserRepositoryCustom {
    // Basic queries
    fun findByUsername(username: String): User?

    fun findByEmail(email: String): User?

    // Queries to check existence
    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean

    // Querie with JOIN FETCH to load roles and groups
    @Query(
        "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles LEFT JOIN FETCH u.groups " +
            "WHERE u.username = :username",
    )
    fun findByUsernameWithRolesAndGroups(
        @Param("username") username: String,
    ): User?

    // Update last login date
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.lastLogin = CURRENT_TIMESTAMP WHERE u.id = :userId")
    fun updateLastLogin(
        @Param("userId") userId: Long,
    )

    // Update password
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    fun updatePassword(
        @Param("userId") userId: Long,
        @Param("newPassword") newPassword: String,
    )

    // Querie active users
    fun findByIsActive(isActive: Boolean): List<User>

    // Querie by name (case-insensitive)
    @Query(
        "SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))",
    )
    fun searchByName(
        @Param("name") name: String,
    ): List<User>
}
