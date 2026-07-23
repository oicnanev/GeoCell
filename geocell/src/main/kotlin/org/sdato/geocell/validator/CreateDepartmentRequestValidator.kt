package org.sdato.geocell.validator

import org.sdato.geocell.dto.request.CreateDepartmentRequest
import org.sdato.geocell.dto.request.UpdateDepartmentRequest
import org.sdato.geocell.exception.ValidationException
import org.springframework.stereotype.Component

@Component
class CreateDepartmentRequestValidator {

	fun validate(request: CreateDepartmentRequest) {
		validateName(request.name)
	}

	fun validate(request: UpdateDepartmentRequest) {
		validateName(request.name)
	}

	private fun validateName(name: String) {
		if (name.isBlank()) {
			throw ValidationException("Department name is required")
		}
		if (name.length > 150) {
			throw ValidationException("Department name must have at most 150 characters")
		}
	}
}
