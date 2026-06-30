package org.sdato.geocell.repository.department

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
//@ConditionalOnBean(JdbcTemplate::class)
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
}
