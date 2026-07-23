package org.sdato.geocell.repository.user

import org.sdato.geocell.domain.user.CreateUserRecord
import org.sdato.geocell.domain.user.UpdateUserRecord
import org.sdato.geocell.domain.user.UserRecord
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
@Profile("!test")
class JdbcUserRepository(
	private val jdbcTemplate: JdbcTemplate
) : UserRepository {

	override fun existsByUsername(username: String): Boolean =
		jdbcTemplate.queryForObject(
			"SELECT EXISTS(SELECT 1 FROM auth_user WHERE lower(username) = lower(?))",
			Boolean::class.java,
			username
		) ?: false

	override fun existsByEmail(email: String): Boolean =
		jdbcTemplate.queryForObject(
			"SELECT EXISTS(SELECT 1 FROM auth_user WHERE lower(email) = lower(?))",
			Boolean::class.java,
			email
		) ?: false

	override fun existsByUsernameExcludingId(username: String, userId: Long): Boolean =
		jdbcTemplate.queryForObject(
			"SELECT EXISTS(SELECT 1 FROM auth_user WHERE lower(username) = lower(?) AND id <> ?)",
			Boolean::class.java,
			username,
			userId
		) ?: false

	override fun existsByEmailExcludingId(email: String, userId: Long): Boolean =
		jdbcTemplate.queryForObject(
			"SELECT EXISTS(SELECT 1 FROM auth_user WHERE lower(email) = lower(?) AND id <> ?)",
			Boolean::class.java,
			email,
			userId
		) ?: false

	override fun createUser(record: CreateUserRecord): Long =
		jdbcTemplate.queryForObject(
			"""
			INSERT INTO auth_user (
				username,
				password,
				name,
				email,
				is_superuser,
				is_analyst,
				is_operation_admin,
				map_type,
				show_grid,
				show_counties
			)
			VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
			RETURNING id
			""".trimIndent(),
			Long::class.java,
			record.username,
			record.passwordHash,
			record.name,
			record.email,
			record.isSuperuser,
			record.isAnalyst,
			record.isOperationAdmin,
			record.mapType,
			record.showGrid,
			record.showCounties
		) ?: throw IllegalStateException("Failed to create user")

	override fun assignDepartment(userId: Long, departmentId: Long) {
		jdbcTemplate.update(
			"INSERT INTO user_department (user_id, department_id) VALUES (?, ?)",
			userId,
			departmentId
		)
	}

	override fun updateUser(userId: Long, record: UpdateUserRecord): Boolean {
		val rowsUpdated = if (record.passwordHash != null) {
			jdbcTemplate.update(
				"""
				UPDATE auth_user
				SET username = ?, password = ?, name = ?, email = ?, is_superuser = ?, is_analyst = ?,
					is_operation_admin = ?, map_type = ?, show_grid = ?, show_counties = ?
				WHERE id = ?
				""".trimIndent(),
				record.username,
				record.passwordHash,
				record.name,
				record.email,
				record.isSuperuser,
				record.isAnalyst,
				record.isOperationAdmin,
				record.mapType,
				record.showGrid,
				record.showCounties,
				userId
			)
		} else {
			jdbcTemplate.update(
				"""
				UPDATE auth_user
				SET username = ?, name = ?, email = ?, is_superuser = ?, is_analyst = ?,
					is_operation_admin = ?, map_type = ?, show_grid = ?, show_counties = ?
				WHERE id = ?
				""".trimIndent(),
				record.username,
				record.name,
				record.email,
				record.isSuperuser,
				record.isAnalyst,
				record.isOperationAdmin,
				record.mapType,
				record.showGrid,
				record.showCounties,
				userId
			)
		}

		return rowsUpdated > 0
	}

	override fun replaceDepartment(userId: Long, departmentId: Long) {
		jdbcTemplate.update("DELETE FROM user_department WHERE user_id = ?", userId)
		assignDepartment(userId, departmentId)
	}

	override fun findAll(): List<UserRecord> =
		jdbcTemplate.query(
			"""
			SELECT
				u.id,
				u.username,
				u.name,
				u.email,
				ud.department_id,
				u.is_active,
				u.is_superuser,
				u.is_analyst,
				u.is_operation_admin,
				u.map_type,
				u.show_grid,
				u.show_counties
			FROM auth_user u
			LEFT JOIN LATERAL (
				SELECT department_id
				FROM user_department
				WHERE user_id = u.id
				ORDER BY id DESC
				LIMIT 1
			) ud ON true
			ORDER BY u.id DESC
			""".trimIndent()
		) { rs, _ -> rs.toUserRecord() }

	override fun findById(userId: Long): UserRecord? =
		jdbcTemplate.query(
			"""
			SELECT
				u.id,
				u.username,
				u.name,
				u.email,
				ud.department_id,
				u.is_active,
				u.is_superuser,
				u.is_analyst,
				u.is_operation_admin,
				u.map_type,
				u.show_grid,
				u.show_counties
			FROM auth_user u
			LEFT JOIN LATERAL (
				SELECT department_id
				FROM user_department
				WHERE user_id = u.id
				ORDER BY id DESC
				LIMIT 1
			) ud ON true
			WHERE u.id = ?
			""".trimIndent(),
			{ rs, _ -> rs.toUserRecord() },
			userId
		).firstOrNull()

	override fun deleteUser(userId: Long): Boolean {
		jdbcTemplate.update(
			"""DELETE FROM "session" WHERE session_key IN (SELECT session_key FROM active_user_session WHERE user_id = ?)""",
			userId
		)
		jdbcTemplate.update("UPDATE cell SET updated_by = NULL WHERE updated_by = ?", userId)
		jdbcTemplate.update("UPDATE cell SET created_by = NULL WHERE created_by = ?", userId)
		listOf(
			"DELETE FROM operation_cell WHERE user_id = ?",
			"DELETE FROM operation_poi WHERE user_id = ?",
			"DELETE FROM operation_user_cell WHERE user_id = ?",
			"DELETE FROM chronological_path_analysis WHERE user_id = ?",
			"DELETE FROM user_location WHERE user_id = ?",
			"DELETE FROM user_activity WHERE user_id = ?"
		).forEach { sql ->
			jdbcTemplate.update(sql, userId)
		}
		return jdbcTemplate.update("DELETE FROM auth_user WHERE id = ?", userId) > 0
	}

	private fun java.sql.ResultSet.toUserRecord(): UserRecord {
		val departmentIdValue = getLong("department_id")
		val departmentId = if (wasNull()) null else departmentIdValue

		return UserRecord(
			id = getLong("id"),
			username = getString("username"),
			name = getString("name"),
			email = getString("email"),
			departmentId = departmentId,
			isActive = getBoolean("is_active"),
			isSuperuser = getBoolean("is_superuser"),
			isAnalyst = getBoolean("is_analyst"),
			isOperationAdmin = getBoolean("is_operation_admin"),
			mapType = getString("map_type"),
			showGrid = getBoolean("show_grid"),
			showCounties = getBoolean("show_counties")
		)
	}
}
