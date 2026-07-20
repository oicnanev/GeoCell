package org.sdato.geocell.dto.response

data class CellsInCircleResponse(
	val centerLatitude: Double,
	val centerLongitude: Double,
	val radiusKm: Double,
	val cells: List<CellResponse>
)
