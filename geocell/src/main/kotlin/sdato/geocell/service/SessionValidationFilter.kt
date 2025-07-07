package sdato.geocell.service

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import sdato.geocell.repository.UserSessionRepository

class SessionValidationFilter(
    private val userSessionRepository: UserSessionRepository,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val sessionToken = request.getHeader("X-Session-Token") ?: request.getParameter("sessionToken")

        if (sessionToken != null) {
            val session = userSessionRepository.findBySessionToken(sessionToken)

            if (session == null || !session.isValid || session.isExpired()) {
                SecurityContextHolder.clearContext()
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired session")
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}
