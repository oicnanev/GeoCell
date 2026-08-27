package org.sdato.geocell.dto.response

data class CellsTouchingPointResponse(
	val latitude: Double,
	val longitude: Double,
	val cells: List<CellResponse>
)
