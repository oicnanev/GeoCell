package sdato.geocell.service

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import sdato.geocell.repository.UserSessionRepository

class CustomLogoutHandler(
    private val userSessionRepository: UserSessionRepository,
) : LogoutHandler {
    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?,
    ) {
        val sessionToken = request.getHeader("X-Session-Token") ?: request.getParameter("sessionToken")

        if (sessionToken != null) {
            userSessionRepository.invalidateSession(sessionToken)
        }
    }
}
