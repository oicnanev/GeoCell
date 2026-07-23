package org.sdato.geocell.service.user

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.sdato.geocell.domain.auth.AuthUserPrincipal
import org.sdato.geocell.domain.user.CreateUserRecord
import org.sdato.geocell.domain.user.UpdateUserRecord
import org.sdato.geocell.domain.user.UserRecord
import org.sdato.geocell.dto.request.UpdateUserRequest
import org.sdato.geocell.exception.ForbiddenOperationException
import org.sdato.geocell.exception.ResourceConflictException
import org.sdato.geocell.exception.ResourceNotFoundException
import org.sdato.geocell.exception.ValidationException
import org.sdato.geocell.repository.department.DepartmentRepository
import org.sdato.geocell.repository.user.UserRepository
import org.sdato.geocell.validator.CreateUserRequestValidator
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserServiceTest {
	private lateinit var userRepository: InMemoryUserRepository
	private lateinit var departmentRepository: InMemoryDepartmentRepository
	private lateinit var passwordEncoder: PasswordEncoder
	private lateinit var service: UserService

	private val superuserPrincipal = AuthUserPrincipal(
		userId = 1,
		usernameValue = "root",
		fullName = "Root User",
		email = "root@example.com",
		isSuperuser = true,
		isAnalyst = false,
		isOperationAdmin = false,
		active = true
	)

	private val analystPrincipal = AuthUserPrincipal(
		userId = 2,
		usernameValue = "analyst",
		fullName = "Analyst User",
		email = "analyst@example.com",
		isSuperuser = false,
		isAnalyst = true,
		isOperationAdmin = false,
		active = true
	)

	@BeforeEach
	fun setUp() {
		userRepository = InMemoryUserRepository()
		departmentRepository = InMemoryDepartmentRepository(
			mutableMapOf(
				1L to "Ops",
				2L to "Analysis"
			)
		)
		passwordEncoder = BCryptPasswordEncoder()
		service = UserService(
			userRepository = userRepository,
			departmentRepository = departmentRepository,
			passwordEncoder = passwordEncoder,
			createUserRequestValidator = CreateUserRequestValidator()
		)

		userRepository.seed(
			StoredUser(
				id = 10L,
				username = "jdoe",
				passwordHash = passwordEncoder.encode("initial-password")
					?: throw IllegalStateException("Password encoder returned null hash"),
				name = "John Doe",
				email = "john@example.com",
				departmentId = 1L,
				isActive = true,
				isSuperuser = false,
				isAnalyst = true,
				isOperationAdmin = false,
				mapType = "standard",
				showGrid = false,
				showCounties = false
			)
		)
		userRepository.seed(
			StoredUser(
				id = 11L,
				username = "maria",
				passwordHash = passwordEncoder.encode("maria-password")
					?: throw IllegalStateException("Password encoder returned null hash"),
				name = "Maria Silva",
				email = "maria@example.com",
				departmentId = 2L,
				isActive = true,
				isSuperuser = false,
				isAnalyst = false,
				isOperationAdmin = false,
				mapType = "standard",
				showGrid = false,
				showCounties = false
			)
		)
	}

	@Test
	fun `update user keeps current username and email and updates department`() {
		val response = service.updateUser(
			10L,
			UpdateUserRequest(
				username = "jdoe",
				password = null,
				name = "John D.",
				email = "john@example.com",
				departmentId = 2L,
				isSuperuser = false,
				isAnalyst = true,
				isOperationAdmin = true,
				mapType = "hybrid",
				showGrid = true,
				showCounties = true
			),
			superuserPrincipal
		)

		assertEquals("jdoe", response.username)
		assertEquals("John D.", response.name)
		assertEquals(2L, response.departmentId)
		assertTrue(response.isOperationAdmin)
		assertEquals("hybrid", response.mapType)
		assertEquals(2L, userRepository.findById(10L)?.departmentId)
		assertTrue(userRepository.passwordMatches(10L, "initial-password", passwordEncoder))
	}

	@Test
	fun `update user replaces password when provided`() {
		service.updateUser(
			10L,
			UpdateUserRequest(
				username = "jdoe",
				password = "updated-password",
				name = "John Doe",
				email = "john@example.com",
				departmentId = 1L,
				isSuperuser = true,
				isAnalyst = true,
				isOperationAdmin = false,
				mapType = "terrain",
				showGrid = false,
				showCounties = true
			),
			superuserPrincipal
		)

		assertTrue(userRepository.passwordMatches(10L, "updated-password", passwordEncoder))
		assertFalse(userRepository.passwordMatches(10L, "initial-password", passwordEncoder))
	}

	@Test
	fun `delete user removes record`() {
		service.deleteUser(10L, superuserPrincipal)

		assertEquals(null, userRepository.findById(10L))
	}

	@Test
	fun `non superusers cannot update or delete users`() {
		assertFailsWith<ForbiddenOperationException> {
			service.updateUser(
				10L,
				UpdateUserRequest(
					username = "jdoe",
					name = "Blocked",
					email = "john@example.com",
					departmentId = 1L
				),
				analystPrincipal
			)
		}

		assertFailsWith<ForbiddenOperationException> {
			service.deleteUser(10L, analystPrincipal)
		}
	}

	@Test
	fun `update user fails when user does not exist`() {
		assertFailsWith<ResourceNotFoundException> {
			service.updateUser(
				99L,
				UpdateUserRequest(
					username = "ghost",
					name = "Ghost User",
					email = "ghost@example.com",
					departmentId = 1L
				),
				superuserPrincipal
			)
		}
	}

	@Test
	fun `update user fails when department does not exist`() {
		assertFailsWith<ResourceNotFoundException> {
			service.updateUser(
				10L,
				UpdateUserRequest(
					username = "jdoe",
					name = "John Doe",
					email = "john@example.com",
					departmentId = 99L
				),
				superuserPrincipal
			)
		}
	}

	@Test
	fun `update user fails on duplicate username or email`() {
		assertFailsWith<ResourceConflictException> {
			service.updateUser(
				10L,
				UpdateUserRequest(
					username = "maria",
					name = "John Doe",
					email = "john@example.com",
					departmentId = 1L
				),
				superuserPrincipal
			)
		}

		assertFailsWith<ResourceConflictException> {
			service.updateUser(
				10L,
				UpdateUserRequest(
					username = "jdoe",
					name = "John Doe",
					email = "maria@example.com",
					departmentId = 1L
				),
				superuserPrincipal
			)
		}
	}

	@Test
	fun `update user validates optional password and payload fields`() {
		assertFailsWith<ValidationException> {
			service.updateUser(
				10L,
				UpdateUserRequest(
					username = "jdoe",
					password = " ",
					name = "John Doe",
					email = "john@example.com",
					departmentId = 1L
				),
				superuserPrincipal
			)
		}

		assertFailsWith<ValidationException> {
			service.updateUser(
				10L,
				UpdateUserRequest(
					username = "",
					name = "John Doe",
					email = "invalid-email",
					departmentId = 1L
				),
				superuserPrincipal
			)
		}
	}

	@Test
	fun `delete user fails when user does not exist`() {
		assertFailsWith<ResourceNotFoundException> {
			service.deleteUser(99L, superuserPrincipal)
		}
	}
}

private data class StoredUser(
	val id: Long,
	val username: String,
	val passwordHash: String,
	val name: String,
	val email: String,
	val departmentId: Long?,
	val isActive: Boolean,
	val isSuperuser: Boolean,
	val isAnalyst: Boolean,
	val isOperationAdmin: Boolean,
	val mapType: String,
	val showGrid: Boolean,
	val showCounties: Boolean
)

private class InMemoryUserRepository : UserRepository {
	private val users = linkedMapOf<Long, StoredUser>()

	fun seed(user: StoredUser) {
		users[user.id] = user
	}

	fun passwordMatches(userId: Long, rawPassword: String, passwordEncoder: PasswordEncoder): Boolean =
		users[userId]?.passwordHash?.let { passwordEncoder.matches(rawPassword, it) } ?: false

	override fun existsByUsername(username: String): Boolean =
		users.values.any { it.username.equals(username, ignoreCase = true) }

	override fun existsByEmail(email: String): Boolean =
		users.values.any { it.email.equals(email, ignoreCase = true) }

	override fun existsByUsernameExcludingId(username: String, userId: Long): Boolean =
		users.values.any { it.id != userId && it.username.equals(username, ignoreCase = true) }

	override fun existsByEmailExcludingId(email: String, userId: Long): Boolean =
		users.values.any { it.id != userId && it.email.equals(email, ignoreCase = true) }

	override fun createUser(record: CreateUserRecord): Long = error("Not used in this test")

	override fun assignDepartment(userId: Long, departmentId: Long) {
		replaceDepartment(userId, departmentId)
	}

	override fun updateUser(userId: Long, record: UpdateUserRecord): Boolean {
		val existing = users[userId] ?: return false
		users[userId] = existing.copy(
			username = record.username,
			passwordHash = record.passwordHash ?: existing.passwordHash,
			name = record.name,
			email = record.email,
			isSuperuser = record.isSuperuser,
			isAnalyst = record.isAnalyst,
			isOperationAdmin = record.isOperationAdmin,
			mapType = record.mapType,
			showGrid = record.showGrid,
			showCounties = record.showCounties
		)
		return true
	}

	override fun replaceDepartment(userId: Long, departmentId: Long) {
		val existing = users[userId] ?: return
		users[userId] = existing.copy(departmentId = departmentId)
	}

	override fun findAll(): List<UserRecord> = users.values.map { it.toRecord() }

	override fun findById(userId: Long): UserRecord? = users[userId]?.toRecord()

	override fun deleteUser(userId: Long): Boolean = users.remove(userId) != null

	private fun StoredUser.toRecord() = UserRecord(
		id = id,
		username = username,
		name = name,
		email = email,
		departmentId = departmentId,
		isActive = isActive,
		isSuperuser = isSuperuser,
		isAnalyst = isAnalyst,
		isOperationAdmin = isOperationAdmin,
		mapType = mapType,
		showGrid = showGrid,
		showCounties = showCounties
	)
}

private class InMemoryDepartmentRepository(
	private val departments: MutableMap<Long, String>
) : DepartmentRepository {
	override fun existsByName(name: String): Boolean =
		departments.values.any { it.equals(name, ignoreCase = true) }

	override fun existsByNameExcludingId(name: String, departmentId: Long): Boolean =
		departments.any { (id, currentName) -> id != departmentId && currentName.equals(name, ignoreCase = true) }

	override fun existsById(id: Long): Boolean = departments.containsKey(id)

	override fun createDepartment(name: String, haveOperations: Boolean): Long = error("Not used in this test")

	override fun findById(id: Long) = error("Not used in this test")

	override fun updateDepartment(id: Long, name: String, haveOperations: Boolean): Boolean = error("Not used in this test")

	override fun countUsersByDepartmentId(id: Long): Long = error("Not used in this test")

	override fun countOperationsByDepartmentId(id: Long): Long = error("Not used in this test")

	override fun deleteDepartment(id: Long): Boolean = error("Not used in this test")

	override fun findAll() = error("Not used in this test")
}
