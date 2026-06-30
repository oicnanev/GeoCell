package org.sdato.geocell.validator

import org.sdato.geocell.dto.request.LoginRequest
import org.sdato.geocell.exception.ValidationException
import org.springframework.stereotype.Component

@Component
class LoginRequestValidator {

	fun validate(request: LoginRequest) {
		if (request.username.isBlank()) {
			throw ValidationException("Username is required")
		}
		if (request.password.isBlank()) {
			throw ValidationException("Password is required")
		}
	}
}
