package org.sdato.geocell.dto.response

data class AuthUserResponse(
	val id: Long,
	val username: String,
	val name: String,
	val email: String,
	val roles: Set<String>
)
