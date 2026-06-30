package org.sdato.geocell.controller.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.sdato.geocell.dto.request.LoginRequest
import org.sdato.geocell.dto.response.AuthUserResponse
import org.sdato.geocell.exception.InvalidCredentialsException
import org.sdato.geocell.service.auth.AuthSessionService
import org.sdato.geocell.util.toResponse
import org.sdato.geocell.validator.LoginRequestValidator
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@ConditionalOnBean(AuthSessionService::class)
@Profile("!test")
class AuthController(
	private val authSessionService: AuthSessionService,
	private val loginRequestValidator: LoginRequestValidator
) {

	@PostMapping("/login")
	fun login(
		@RequestBody request: LoginRequest,
		servletRequest: HttpServletRequest,
		response: HttpServletResponse
	): AuthUserResponse {
		loginRequestValidator.validate(request)
		val loginResult = authSessionService.login(
			username = request.username,
			rawPassword = request.password,
			path = servletRequest.requestURI,
			ipAddress = servletRequest.remoteAddr,
			userAgent = servletRequest.getHeader("User-Agent")
		) ?: throw InvalidCredentialsException()

		response.addHeader(AuthSessionService.SET_COOKIE_HEADER, loginResult.cookieHeader)
		return loginResult.principal.toResponse()
	}

	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun logout(servletRequest: HttpServletRequest, response: HttpServletResponse) {
		val sessionKey = servletRequest.cookies
			?.firstOrNull { it.name == AuthSessionService.SESSION_COOKIE_NAME }
			?.value
			?: return

		response.addHeader(AuthSessionService.SET_COOKIE_HEADER, authSessionService.logout(sessionKey))
	}

	@GetMapping("/me")
	fun me(authentication: Authentication): AuthUserResponse {
		val principal = authSessionService.extractPrincipal(authentication) ?: throw InvalidCredentialsException()
		return principal.toResponse()
	}
}
