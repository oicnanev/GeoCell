package org.sdato.geocell.validator

import org.sdato.geocell.dto.request.CreateUserRequest
import org.sdato.geocell.dto.request.UpdateUserRequest
import org.sdato.geocell.exception.ValidationException
import org.springframework.stereotype.Component

@Component
class CreateUserRequestValidator {

	private val supportedMapTypes = setOf("standard", "satellite", "hybrid", "terrain")
	private val emailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

	fun validate(request: CreateUserRequest) {
		validateCommon(
			username = request.username,
			password = request.password,
			name = request.name,
			email = request.email,
			mapType = request.mapType,
			passwordRequired = true
		)
	}

	fun validate(request: UpdateUserRequest) {
		validateCommon(
			username = request.username,
			password = request.password,
			name = request.name,
			email = request.email,
			mapType = request.mapType,
			passwordRequired = false
		)
	}

	private fun validateCommon(
		username: String,
		password: String?,
		name: String,
		email: String,
		mapType: String,
		passwordRequired: Boolean
	) {
		if (username.isBlank()) {
			throw ValidationException("Username is required")
		}
		if (username.length > 150) {
			throw ValidationException("Username must have at most 150 characters")
		}
		if (passwordRequired && password.isNullOrBlank()) {
			throw ValidationException("Password is required")
		}
		if (password != null) {
			if (password.isBlank()) {
				throw ValidationException("Password cannot be blank")
			}
			if (password.length > 128) {
				throw ValidationException("Password must have at most 128 characters")
			}
		}
		if (name.isBlank()) {
			throw ValidationException("Name is required")
		}
		if (name.length > 150) {
			throw ValidationException("Name must have at most 150 characters")
		}
		if (email.isBlank()) {
			throw ValidationException("Email is required")
		}
		if (email.length > 254 || !emailRegex.matches(email)) {
			throw ValidationException("Email format is invalid")
		}
		if (mapType !in supportedMapTypes) {
			throw ValidationException("Map type is invalid")
		}
	}
}
