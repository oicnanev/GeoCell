package org.sdato.geocell.domain.user

data class UserRecord(
	val id: Long,
	val username: String,
	val name: String,
	val email: String,
	val departmentId: Long?,
	val isActive: Boolean,
	val isSuperuser: Boolean,
	val isAnalyst: Boolean,
	val isOperationAdmin: Boolean,
	val mapType: String,
	val showGrid: Boolean,
	val showCounties: Boolean
)
