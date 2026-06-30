package org.sdato.geocell.domain.user

data class CreateUserRecord(
	val username: String,
	val passwordHash: String,
	val name: String,
	val email: String,
	val isSuperuser: Boolean,
	val isAnalyst: Boolean,
	val isOperationAdmin: Boolean,
	val mapType: String,
	val showGrid: Boolean,
	val showCounties: Boolean
)
