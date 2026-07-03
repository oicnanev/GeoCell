package org.sdato.geocell.validator

import org.sdato.geocell.dto.request.CreateUserRequest
import org.sdato.geocell.exception.ValidationException
import org.springframework.stereotype.Component

@Component
class CreateUserRequestValidator {

	private val supportedMapTypes = setOf("standard", "satellite", "hybrid", "terrain")
	private val emailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

	fun validate(request: CreateUserRequest) {
		if (request.username.isBlank()) {
			throw ValidationException("Username is required")
		}
		if (request.username.length > 150) {
			throw ValidationException("Username must have at most 150 characters")
		}
		if (request.password.isBlank()) {
			throw ValidationException("Password is required")
		}
		if (request.password.length > 128) {
			throw ValidationException("Password must have at most 128 characters")
		}
		if (request.name.isBlank()) {
			throw ValidationException("Name is required")
		}
		if (request.name.length > 150) {
			throw ValidationException("Name must have at most 150 characters")
		}
		if (request.email.isBlank()) {
			throw ValidationException("Email is required")
		}
		if (request.email.length > 254 || !emailRegex.matches(request.email)) {
			throw ValidationException("Email format is invalid")
		}
		if (request.mapType !in supportedMapTypes) {
			throw ValidationException("Map type is invalid")
		}
	}
}
