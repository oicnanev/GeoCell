package org.sdato.geocell.dto.response

data class CreateUserResponse(
	val id: Long,
	val username: String,
	val name: String,
	val email: String,
	val departmentId: Long,
	val isSuperuser: Boolean,
	val isAnalyst: Boolean,
	val isOperationAdmin: Boolean,
	val mapType: String,
	val showGrid: Boolean,
	val showCounties: Boolean
)
