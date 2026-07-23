package org.sdato.geocell.service.department

import org.sdato.geocell.domain.auth.AuthUserPrincipal
import org.sdato.geocell.dto.request.CreateDepartmentRequest
import org.sdato.geocell.dto.request.UpdateDepartmentRequest
import org.sdato.geocell.dto.response.CreateDepartmentResponse
import org.sdato.geocell.exception.ForbiddenOperationException
import org.sdato.geocell.exception.ResourceConflictException
import org.sdato.geocell.exception.ResourceNotFoundException
import org.sdato.geocell.repository.department.DepartmentRepository
import org.sdato.geocell.validator.CreateDepartmentRequestValidator
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("!test")
class DepartmentService(
	private val departmentRepository: DepartmentRepository,
	private val createDepartmentRequestValidator: CreateDepartmentRequestValidator
) {
	fun createDepartment(request: CreateDepartmentRequest, principal: AuthUserPrincipal): CreateDepartmentResponse {
		assertSuperuser(principal)
		createDepartmentRequestValidator.validate(request)

		val normalizedName = request.name.trim()
		if (departmentRepository.existsByName(normalizedName)) {
			throw ResourceConflictException("Department '$normalizedName' already exists")
		}

		val id = departmentRepository.createDepartment(normalizedName, request.haveOperations)
		return CreateDepartmentResponse(
			id = id,
			name = normalizedName,
			haveOperations = request.haveOperations
		)
	}

	fun listDepartments(principal: AuthUserPrincipal): List<CreateDepartmentResponse> {
		assertSuperuser(principal)
		return departmentRepository.findAll().map(::toResponse)
	}

	fun updateDepartment(
		departmentId: Long,
		request: UpdateDepartmentRequest,
		principal: AuthUserPrincipal
	): CreateDepartmentResponse {
		assertSuperuser(principal)
		createDepartmentRequestValidator.validate(request)
		departmentRepository.findById(departmentId)
			?: throw ResourceNotFoundException("Department with id $departmentId was not found")

		val normalizedName = request.name.trim()
		if (departmentRepository.existsByNameExcludingId(normalizedName, departmentId)) {
			throw ResourceConflictException("Department '$normalizedName' already exists")
		}

		departmentRepository.updateDepartment(departmentId, normalizedName, request.haveOperations)
		return CreateDepartmentResponse(
			id = departmentId,
			name = normalizedName,
			haveOperations = request.haveOperations
		)
	}

	fun deleteDepartment(departmentId: Long, principal: AuthUserPrincipal) {
		assertSuperuser(principal)
		departmentRepository.findById(departmentId)
			?: throw ResourceNotFoundException("Department with id $departmentId was not found")

		if (departmentRepository.countUsersByDepartmentId(departmentId) > 0) {
			throw ResourceConflictException("Department with id $departmentId cannot be deleted while users are assigned to it")
		}
		if (departmentRepository.countOperationsByDepartmentId(departmentId) > 0) {
			throw ResourceConflictException("Department with id $departmentId cannot be deleted while operations are linked to it")
		}

		departmentRepository.deleteDepartment(departmentId)
	}

	private fun assertSuperuser(principal: AuthUserPrincipal) {
		if (!principal.isSuperuser) {
			throw ForbiddenOperationException("Only superusers can manage departments")
		}
	}

	private fun toResponse(department: org.sdato.geocell.domain.department.DepartmentRecord) =
		CreateDepartmentResponse(
			id = department.id,
			name = department.name,
			haveOperations = department.haveOperations
		)
}
