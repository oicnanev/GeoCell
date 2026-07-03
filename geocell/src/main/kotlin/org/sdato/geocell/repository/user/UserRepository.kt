package org.sdato.geocell.repository.user

import org.sdato.geocell.domain.user.CreateUserRecord
import org.sdato.geocell.domain.user.UserRecord

interface UserRepository {
	fun existsByUsername(username: String): Boolean
	fun existsByEmail(email: String): Boolean
	fun createUser(record: CreateUserRecord): Long
	fun assignDepartment(userId: Long, departmentId: Long)
	fun findAll(): List<UserRecord>
	fun findById(userId: Long): UserRecord?
}
