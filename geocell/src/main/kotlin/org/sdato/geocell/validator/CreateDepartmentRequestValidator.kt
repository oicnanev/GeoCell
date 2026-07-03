package org.sdato.geocell.validator

import org.sdato.geocell.dto.request.CreateDepartmentRequest
import org.sdato.geocell.exception.ValidationException
import org.springframework.stereotype.Component

@Component
class CreateDepartmentRequestValidator {

	fun validate(request: CreateDepartmentRequest) {
		if (request.name.isBlank()) {
			throw ValidationException("Department name is required")
		}
		if (request.name.length > 150) {
			throw ValidationException("Department name must have at most 150 characters")
		}
	}
}
