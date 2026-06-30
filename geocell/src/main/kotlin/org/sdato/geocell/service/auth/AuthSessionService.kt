package org.sdato.geocell.service.auth

import org.springframework.beans.factory.annotation.Value
import org.sdato.geocell.domain.auth.AuthUserPrincipal
import org.sdato.geocell.domain.auth.LoginResult
import org.sdato.geocell.repository.auth.AuthSessionRepository
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.time.Duration
import java.time.OffsetDateTime

@Service
@Profile("!test")
class AuthSessionService(
	private val authSessionRepository: AuthSessionRepository,
	private val passwordEncoder: PasswordEncoder,
	@Value("\${app.security.session.ttl-hours:24}") private val sessionTtlHours: Long,
	@Value("\${app.security.session.secure-cookie:false}") private val secureCookie: Boolean
) {
	private val random = SecureRandom()

	fun login(username: String, rawPassword: String, path: String, ipAddress: String?, userAgent: String?): LoginResult? {
		val userRecord = authSessionRepository.findUserByUsername(username) ?: return null
		if (!userRecord.active || !passwordEncoder.matches(rawPassword, userRecord.passwordHash)) return null

		val existingSessionKey = authSessionRepository.findActiveSessionKeyByUserId(userRecord.userId)
		if (existingSessionKey != null) {
			authSessionRepository.deleteActiveSession(existingSessionKey)
			authSessionRepository.deleteSession(existingSessionKey)
		}

		val sessionKey = generateSessionKey()
		val expiresAt = OffsetDateTime.now().plusHours(sessionTtlHours)
		val serializedSession = """{"userId":${userRecord.userId}}""".toByteArray(StandardCharsets.UTF_8)

		authSessionRepository.createSession(sessionKey, serializedSession, expiresAt)
		authSessionRepository.upsertActiveUserSession(userRecord.userId, sessionKey, ipAddress, userAgent)
		authSessionRepository.updateLastLogin(userRecord.userId)
		authSessionRepository.upsertUserActivity(userRecord.userId, path, ipAddress)

		return LoginResult(
			cookieHeader = createSessionCookie(sessionKey, Duration.ofHours(sessionTtlHours)).toString(),
			principal = userRecord.toPrincipal()
		)
	}

	fun logout(sessionKey: String): String {
		authSessionRepository.deleteActiveSession(sessionKey)
		authSessionRepository.deleteSession(sessionKey)
		return clearSessionCookie().toString()
	}

	fun resolveUserFromSessionKey(sessionKey: String): AuthUserPrincipal? =
		authSessionRepository.findUserBySessionKey(sessionKey)?.toPrincipal()

	fun touchSession(sessionKey: String, path: String, ipAddress: String?) {
		val user = authSessionRepository.findUserBySessionKey(sessionKey) ?: return
		authSessionRepository.touchHeartbeat(sessionKey)
		authSessionRepository.upsertUserActivity(user.userId, path, ipAddress)
	}

	fun createSessionCookieHeader(sessionKey: String): String =
		createSessionCookie(sessionKey, Duration.ofHours(sessionTtlHours)).toString()

	fun extractPrincipal(authentication: Authentication): AuthUserPrincipal? =
		authentication.principal as? AuthUserPrincipal

	private fun createSessionCookie(sessionKey: String, maxAge: Duration): ResponseCookie =
		ResponseCookie.from(SESSION_COOKIE_NAME, sessionKey)
			.httpOnly(true)
			.secure(secureCookie)
			.sameSite("Lax")
			.path("/")
			.maxAge(maxAge)
			.build()

	private fun clearSessionCookie(): ResponseCookie =
		ResponseCookie.from(SESSION_COOKIE_NAME, "")
			.httpOnly(true)
			.secure(secureCookie)
			.sameSite("Lax")
			.path("/")
			.maxAge(Duration.ZERO)
			.build()

	private fun generateSessionKey(): String {
		val bytes = ByteArray(20)
		random.nextBytes(bytes)
		return bytes.joinToString(separator = "") { b -> "%02x".format(b) }
	}

	companion object {
		const val SESSION_COOKIE_NAME = "session_key"
		const val SET_COOKIE_HEADER = HttpHeaders.SET_COOKIE
	}
}
