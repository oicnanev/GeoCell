package org.sdato.geocell.domain.auth

data class AuthUserPrincipalRecord(
	val userId: Long,
	val username: String,
	val passwordHash: String,
	val fullName: String,
	val email: String,
	val active: Boolean,
	val superuser: Boolean,
	val analyst: Boolean,
	val operationAdmin: Boolean
) {
	fun toPrincipal() = AuthUserPrincipal(
		userId = userId,
		usernameValue = username,
		fullName = fullName,
		email = email,
		isSuperuser = superuser,
		isAnalyst = analyst,
		isOperationAdmin = operationAdmin,
		active = active
	)
}
