package org.sdato.geocell.dto.request

data class UpdateUserRequest(
	val username: String,
	val password: String? = null,
	val name: String,
	val email: String,
	val departmentId: Long,
	val isSuperuser: Boolean = false,
	val isAnalyst: Boolean = false,
	val isOperationAdmin: Boolean = false,
	val mapType: String = "standard",
	val showGrid: Boolean = false,
	val showCounties: Boolean = false
)
