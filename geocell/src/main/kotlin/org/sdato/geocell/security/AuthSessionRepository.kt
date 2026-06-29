package org.sdato.geocell.security

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.OffsetDateTime

@Repository
@ConditionalOnBean(JdbcTemplate::class)
@Profile("!test")
class AuthSessionRepository(
	private val jdbcTemplate: JdbcTemplate
) {

	fun findUserByUsername(username: String): AuthUserPrincipalRecord? =
		jdbcTemplate.query(
			"""
			SELECT id, username, password, name, email, is_active, is_superuser, is_analyst, is_operation_admin
			FROM auth_user
			WHERE username = ?
			""".trimIndent(),
			{ rs, _ ->
				AuthUserPrincipalRecord(
					userId = rs.getLong("id"),
					username = rs.getString("username"),
					passwordHash = rs.getString("password"),
					fullName = rs.getString("name"),
					email = rs.getString("email"),
					active = rs.getBoolean("is_active"),
					superuser = rs.getBoolean("is_superuser"),
					analyst = rs.getBoolean("is_analyst"),
					operationAdmin = rs.getBoolean("is_operation_admin")
				)
			},
			username
		).firstOrNull()

	fun findUserBySessionKey(sessionKey: String): AuthUserPrincipalRecord? =
		jdbcTemplate.query(
			"""
			SELECT u.id, u.username, u.password, u.name, u.email, u.is_active, u.is_superuser, u.is_analyst, u.is_operation_admin
			FROM auth_user u
			JOIN active_user_session aus ON aus.user_id = u.id
			JOIN "session" s ON s.session_key = aus.session_key
			WHERE s.session_key = ? AND s.expire_date > CURRENT_TIMESTAMP
			""".trimIndent(),
			{ rs, _ ->
				AuthUserPrincipalRecord(
					userId = rs.getLong("id"),
					username = rs.getString("username"),
					passwordHash = rs.getString("password"),
					fullName = rs.getString("name"),
					email = rs.getString("email"),
					active = rs.getBoolean("is_active"),
					superuser = rs.getBoolean("is_superuser"),
					analyst = rs.getBoolean("is_analyst"),
					operationAdmin = rs.getBoolean("is_operation_admin")
				)
			},
			sessionKey
		).firstOrNull()

	fun findActiveSessionKeyByUserId(userId: Long): String? =
		jdbcTemplate.query(
			"SELECT session_key FROM active_user_session WHERE user_id = ?",
			{ rs, _ -> rs.getString("session_key") },
			userId
		).firstOrNull()

	fun createSession(sessionKey: String, sessionData: ByteArray, expireDate: OffsetDateTime) {
		jdbcTemplate.update(
			"""INSERT INTO "session" (session_key, session_data, expire_date) VALUES (?, ?, ?)""",
			sessionKey,
			sessionData,
			Timestamp.from(expireDate.toInstant())
		)
	}

	fun upsertActiveUserSession(
		userId: Long,
		sessionKey: String,
		ipAddress: String?,
		userAgent: String?
	) {
		jdbcTemplate.update(
			"""
			INSERT INTO active_user_session (user_id, session_key, last_heartbeat, ip_address, user_agent, sse_connection_count)
			VALUES (?, ?, CURRENT_TIMESTAMP, ?::inet, ?, 0)
			ON CONFLICT (user_id)
			DO UPDATE SET
				session_key = EXCLUDED.session_key,
				last_heartbeat = CURRENT_TIMESTAMP,
				ip_address = EXCLUDED.ip_address,
				user_agent = EXCLUDED.user_agent
			""".trimIndent(),
			userId,
			sessionKey,
			ipAddress,
			userAgent
		)
	}

	fun updateLastLogin(userId: Long) {
		jdbcTemplate.update(
			"UPDATE auth_user SET last_login = CURRENT_TIMESTAMP WHERE id = ?",
			userId
		)
	}

	fun upsertUserActivity(userId: Long, path: String, ipAddress: String?) {
		jdbcTemplate.update(
			"""
			INSERT INTO user_activity (user_id, last_seen, last_path, last_ip)
			VALUES (?, CURRENT_TIMESTAMP, ?, ?::inet)
			ON CONFLICT (user_id)
			DO UPDATE SET
				last_seen = CURRENT_TIMESTAMP,
				last_path = EXCLUDED.last_path,
				last_ip = EXCLUDED.last_ip
			""".trimIndent(),
			userId,
			path,
			ipAddress
		)
	}

	fun touchHeartbeat(sessionKey: String) {
		jdbcTemplate.update(
			"UPDATE active_user_session SET last_heartbeat = CURRENT_TIMESTAMP WHERE session_key = ?",
			sessionKey
		)
	}

	fun deleteActiveSession(sessionKey: String) {
		jdbcTemplate.update(
			"DELETE FROM active_user_session WHERE session_key = ?",
			sessionKey
		)
	}

	fun deleteSession(sessionKey: String) {
		jdbcTemplate.update(
			"""DELETE FROM "session" WHERE session_key = ?""",
			sessionKey
		)
	}
}

data class AuthUserPrincipalRecord(
	val userId: Long,
	val username: String,
	val passwordHash: String,
	val fullName: String,
	val email: String,
	val active: Boolean,
	val superuser: Boolean,
	val analyst: Boolean,
	val operationAdmin: Boolean
) {
	fun toPrincipal() = AuthUserPrincipal(
		userId = userId,
		usernameValue = username,
		fullName = fullName,
		email = email,
		isSuperuser = superuser,
		isAnalyst = analyst,
		isOperationAdmin = operationAdmin,
		active = active
	)
}
