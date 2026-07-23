package org.sdato.geocell.repository.department

import org.sdato.geocell.domain.department.DepartmentRecord

interface DepartmentRepository {
	fun existsByName(name: String): Boolean
	fun existsByNameExcludingId(name: String, departmentId: Long): Boolean
	fun existsById(id: Long): Boolean
	fun createDepartment(name: String, haveOperations: Boolean): Long
	fun findById(id: Long): DepartmentRecord?
	fun updateDepartment(id: Long, name: String, haveOperations: Boolean): Boolean
	fun countUsersByDepartmentId(id: Long): Long
	fun countOperationsByDepartmentId(id: Long): Long
	fun deleteDepartment(id: Long): Boolean
	fun findAll(): List<DepartmentRecord>
}
