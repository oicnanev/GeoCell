package org.sdato.geocell.controller.department

import org.sdato.geocell.domain.auth.AuthUserPrincipal
import org.sdato.geocell.dto.request.CreateDepartmentRequest
import org.sdato.geocell.dto.request.UpdateDepartmentRequest
import org.sdato.geocell.dto.response.CreateDepartmentResponse
import org.sdato.geocell.exception.InvalidCredentialsException
import org.sdato.geocell.service.department.DepartmentService
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/departments")
@Profile("!test")
class DepartmentController(
	private val departmentService: DepartmentService
) {
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	fun createDepartment(
		@RequestBody request: CreateDepartmentRequest,
		authentication: Authentication
	): CreateDepartmentResponse =
		departmentService.createDepartment(request, requirePrincipal(authentication))

	@GetMapping
	fun listDepartments(authentication: Authentication): List<CreateDepartmentResponse> =
		departmentService.listDepartments(requirePrincipal(authentication))

	@PutMapping("/{id}")
	fun updateDepartment(
		@PathVariable id: Long,
		@RequestBody request: UpdateDepartmentRequest,
		authentication: Authentication
	): CreateDepartmentResponse =
		departmentService.updateDepartment(id, request, requirePrincipal(authentication))

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun deleteDepartment(
		@PathVariable id: Long,
		authentication: Authentication
	) {
		departmentService.deleteDepartment(id, requirePrincipal(authentication))
	}

	private fun requirePrincipal(authentication: Authentication): AuthUserPrincipal =
		authentication.principal as? AuthUserPrincipal ?: throw InvalidCredentialsException()
}
