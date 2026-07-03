package org.sdato.geocell.repository.department

import org.sdato.geocell.domain.department.DepartmentRecord

interface DepartmentRepository {
	fun existsByName(name: String): Boolean
	fun existsById(id: Long): Boolean
	fun createDepartment(name: String, haveOperations: Boolean): Long
	fun findAll(): List<DepartmentRecord>
}
