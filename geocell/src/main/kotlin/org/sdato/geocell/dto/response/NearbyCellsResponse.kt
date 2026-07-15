package org.sdato.geocell.dto.response

data class NearbyCellsResponse(
	val centralCell: CellResponse,
	val cellsInRadius: List<CellResponse>
)
