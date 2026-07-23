package org.sdato.geocell.service.department

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.sdato.geocell.domain.auth.AuthUserPrincipal
import org.sdato.geocell.domain.department.DepartmentRecord
import org.sdato.geocell.dto.request.UpdateDepartmentRequest
import org.sdato.geocell.exception.ForbiddenOperationException
import org.sdato.geocell.exception.ResourceConflictException
import org.sdato.geocell.exception.ResourceNotFoundException
import org.sdato.geocell.exception.ValidationException
import org.sdato.geocell.repository.department.DepartmentRepository
import org.sdato.geocell.validator.CreateDepartmentRequestValidator
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DepartmentServiceTest {
	private lateinit var repository: InMemoryDepartmentRepository
	private lateinit var service: DepartmentService

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

	private val regularPrincipal = AuthUserPrincipal(
		userId = 2,
		usernameValue = "user",
		fullName = "Regular User",
		email = "user@example.com",
		isSuperuser = false,
		isAnalyst = true,
		isOperationAdmin = false,
		active = true
	)

	@BeforeEach
	fun setUp() {
		repository = InMemoryDepartmentRepository(
			mutableMapOf(
				1L to DepartmentRecord(1L, "Operations", true),
				2L to DepartmentRecord(2L, "Analysis", false)
			),
			mutableMapOf(
				1L to 0L,
				2L to 0L
			),
			mutableMapOf(
				1L to 0L,
				2L to 0L
			)
		)
		service = DepartmentService(repository, CreateDepartmentRequestValidator())
	}

	@Test
	fun `update department edits name and operations flag`() {
		val response = service.updateDepartment(
			2L,
			UpdateDepartmentRequest(name = "Field Analysis", haveOperations = true),
			superuserPrincipal
		)

		assertEquals("Field Analysis", response.name)
		assertEquals(true, response.haveOperations)
		assertEquals("Field Analysis", repository.findById(2L)?.name)
		assertEquals(true, repository.findById(2L)?.haveOperations)
	}

	@Test
	fun `delete department rejects linked users or operations`() {
		repository.userCounts[1L] = 1L
		assertFailsWith<ResourceConflictException> {
			service.deleteDepartment(1L, superuserPrincipal)
		}

		repository.userCounts[1L] = 0L
		repository.operationCounts[1L] = 2L
		assertFailsWith<ResourceConflictException> {
			service.deleteDepartment(1L, superuserPrincipal)
		}
	}

	@Test
	fun `delete department removes unused department`() {
		service.deleteDepartment(2L, superuserPrincipal)

		assertEquals(null, repository.findById(2L))
	}

	@Test
	fun `non superusers cannot update or delete departments`() {
		assertFailsWith<ForbiddenOperationException> {
			service.updateDepartment(1L, UpdateDepartmentRequest("Blocked", true), regularPrincipal)
		}

		assertFailsWith<ForbiddenOperationException> {
			service.deleteDepartment(1L, regularPrincipal)
		}
	}

	@Test
	fun `update department fails when department does not exist`() {
		assertFailsWith<ResourceNotFoundException> {
			service.updateDepartment(99L, UpdateDepartmentRequest("Ghost", false), superuserPrincipal)
		}
	}

	@Test
	fun `update department fails on duplicate name`() {
		assertFailsWith<ResourceConflictException> {
			service.updateDepartment(2L, UpdateDepartmentRequest("Operations", false), superuserPrincipal)
		}
	}

	@Test
	fun `update department validates payload`() {
		assertFailsWith<ValidationException> {
			service.updateDepartment(1L, UpdateDepartmentRequest(" ", true), superuserPrincipal)
		}
	}

	@Test
	fun `delete department fails when department does not exist`() {
		assertFailsWith<ResourceNotFoundException> {
			service.deleteDepartment(99L, superuserPrincipal)
		}
	}
}

private class InMemoryDepartmentRepository(
	private val departments: MutableMap<Long, DepartmentRecord>,
	val userCounts: MutableMap<Long, Long>,
	val operationCounts: MutableMap<Long, Long>
) : DepartmentRepository {
	override fun existsByName(name: String): Boolean =
		departments.values.any { it.name.equals(name, ignoreCase = true) }

	override fun existsByNameExcludingId(name: String, departmentId: Long): Boolean =
		departments.values.any { it.id != departmentId && it.name.equals(name, ignoreCase = true) }

	override fun existsById(id: Long): Boolean = departments.containsKey(id)

	override fun createDepartment(name: String, haveOperations: Boolean): Long = error("Not used in this test")

	override fun findById(id: Long): DepartmentRecord? = departments[id]

	override fun updateDepartment(id: Long, name: String, haveOperations: Boolean): Boolean {
		val existing = departments[id] ?: return false
		departments[id] = existing.copy(name = name, haveOperations = haveOperations)
		return true
	}

	override fun countUsersByDepartmentId(id: Long): Long = userCounts[id] ?: 0L

	override fun countOperationsByDepartmentId(id: Long): Long = operationCounts[id] ?: 0L

	override fun deleteDepartment(id: Long): Boolean = departments.remove(id) != null

	override fun findAll(): List<DepartmentRecord> = departments.values.toList()
}
