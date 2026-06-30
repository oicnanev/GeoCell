package org.sdato.geocell.dto.response

data class CreateDepartmentResponse(
	val id: Long,
	val name: String,
	val haveOperations: Boolean
)
