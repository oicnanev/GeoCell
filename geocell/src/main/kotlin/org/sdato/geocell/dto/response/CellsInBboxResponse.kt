package org.sdato.geocell.dto.response

data class CellsInBboxResponse(
	val corner1Latitude: Double,
	val corner1Longitude: Double,
	val corner2Latitude: Double,
	val corner2Longitude: Double,
	val cells: List<CellResponse>
)
