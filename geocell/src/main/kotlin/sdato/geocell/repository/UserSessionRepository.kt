package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import sdato.geocell.model.UserSession

@Repository
interface UserSessionRepository : JpaRepository<UserSession, String> {
    fun findBySessionToken(sessionToken: String): UserSession?

    fun findByUserId(userId: Long): List<UserSession>

    @Transactional
    @Modifying
    @Query("UPDATE UserSession us SET us.isValid = false WHERE us.user.id = :userId")
    fun invalidateAllSessionsForUser(userId: Long)

    @Transactional
    @Modifying
    @Query("UPDATE UserSession us SET us.isValid = false WHERE us.sessionToken = :sessionToken")
    fun invalidateSession(sessionToken: String)
}
