package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.Session

interface SessionRepository : JpaRepository<Session, String> {
    fun findBySessionKey(sessionKey: String): Session?
}
