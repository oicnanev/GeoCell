package org.sdato.geocell.repository.department

import org.sdato.geocell.domain.department.DepartmentRecord
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
@Profile("!test")
class JdbcDepartmentRepository(
	private val jdbcTemplate: JdbcTemplate
) : DepartmentRepository {

	override fun existsByName(name: String): Boolean =
		jdbcTemplate.queryForObject(
			"SELECT EXISTS(SELECT 1 FROM department WHERE lower(name) = lower(?))",
			Boolean::class.java,
			name
		) ?: false

	override fun existsByNameExcludingId(name: String, departmentId: Long): Boolean =
		jdbcTemplate.queryForObject(
			"SELECT EXISTS(SELECT 1 FROM department WHERE lower(name) = lower(?) AND id <> ?)",
			Boolean::class.java,
			name,
			departmentId
		) ?: false

	override fun existsById(id: Long): Boolean =
		jdbcTemplate.queryForObject(
			"SELECT EXISTS(SELECT 1 FROM department WHERE id = ?)",
			Boolean::class.java,
			id
		) ?: false

	override fun createDepartment(name: String, haveOperations: Boolean): Long =
		jdbcTemplate.queryForObject(
			"INSERT INTO department (name, have_operations) VALUES (?, ?) RETURNING id",
			Long::class.java,
			name,
			haveOperations
		) ?: throw IllegalStateException("Failed to create department")

	override fun findById(id: Long): DepartmentRecord? =
		jdbcTemplate.query(
			"""
			SELECT id, name, have_operations
			FROM department
			WHERE id = ?
			""".trimIndent(),
			{ rs, _ ->
				DepartmentRecord(
					id = rs.getLong("id"),
					name = rs.getString("name"),
					haveOperations = rs.getBoolean("have_operations")
				)
			},
			id
		).firstOrNull()

	override fun updateDepartment(id: Long, name: String, haveOperations: Boolean): Boolean =
		jdbcTemplate.update(
			"UPDATE department SET name = ?, have_operations = ? WHERE id = ?",
			name,
			haveOperations,
			id
		) > 0

	override fun countUsersByDepartmentId(id: Long): Long =
		jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM user_department WHERE department_id = ?",
			Long::class.java,
			id
		) ?: 0L

	override fun countOperationsByDepartmentId(id: Long): Long =
		jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM operation WHERE department_id = ?",
			Long::class.java,
			id
		) ?: 0L

	override fun deleteDepartment(id: Long): Boolean =
		jdbcTemplate.update("DELETE FROM department WHERE id = ?", id) > 0

	override fun findAll(): List<DepartmentRecord> =
		jdbcTemplate.query(
			"""
			SELECT id, name, have_operations
			FROM department
			ORDER BY name ASC
			""".trimIndent()
		) { rs, _ ->
			DepartmentRecord(
				id = rs.getLong("id"),
				name = rs.getString("name"),
				haveOperations = rs.getBoolean("have_operations")
			)
		}
}
