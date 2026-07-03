package org.sdato.geocell.service.department

import org.sdato.geocell.domain.auth.AuthUserPrincipal
import org.sdato.geocell.dto.request.CreateDepartmentRequest
import org.sdato.geocell.dto.response.CreateDepartmentResponse
import org.sdato.geocell.exception.ForbiddenOperationException
import org.sdato.geocell.exception.ResourceConflictException
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
		return departmentRepository.findAll().map {
			CreateDepartmentResponse(
				id = it.id,
				name = it.name,
				haveOperations = it.haveOperations
			)
		}
	}

	private fun assertSuperuser(principal: AuthUserPrincipal) {
		if (!principal.isSuperuser) {
			throw ForbiddenOperationException("Only superusers can manage departments")
		}
	}
}
