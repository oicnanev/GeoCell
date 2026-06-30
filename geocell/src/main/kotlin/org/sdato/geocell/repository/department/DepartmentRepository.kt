package org.sdato.geocell.repository.department

interface DepartmentRepository {
	fun existsByName(name: String): Boolean
	fun existsById(id: Long): Boolean
	fun createDepartment(name: String, haveOperations: Boolean): Long
}
