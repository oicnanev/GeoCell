package org.sdato.geocell.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@ConditionalOnBean(AuthSessionService::class)
@Profile("!test")
class SessionCookieAuthenticationFilter(
	private val authSessionService: AuthSessionService
) : OncePerRequestFilter() {

	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain
	) {
		val sessionKey = request.cookies
			?.firstOrNull { it.name == AuthSessionService.SESSION_COOKIE_NAME }
			?.value

		if (
			sessionKey != null &&
			SecurityContextHolder.getContext().authentication == null
		) {
			val principal = authSessionService.resolveUserFromSessionKey(sessionKey)
			if (principal != null) {
				val authentication = UsernamePasswordAuthenticationToken(
					principal,
					null,
					principal.authorities
				)
				SecurityContextHolder.getContext().authentication = authentication
				authSessionService.touchSession(sessionKey, request.requestURI, request.remoteAddr)
			}
		}

		filterChain.doFilter(request, response)
	}
}
