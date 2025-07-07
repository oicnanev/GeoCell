package sdato.geocell.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sdato.geocell.repository.UserSessionRepository

@Service
class UserSessionService(
    private val userSessionRepository: UserSessionRepository,
) {
    @Transactional
    fun invalidateAllSessionsForUser(userId: Long) {
        userSessionRepository.invalidateAllSessionsForUser(userId)
    }

    @Transactional
    fun invalidateSession(sessionToken: String) {
        userSessionRepository.invalidateSession(sessionToken)
    }
}
