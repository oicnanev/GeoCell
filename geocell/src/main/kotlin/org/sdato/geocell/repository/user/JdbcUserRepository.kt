package org.sdato.geocell.repository.user

import org.sdato.geocell.domain.user.CreateUserRecord
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
