package org.sdato.geocell.dto.response

data class CellsTouchingCellPolygonResponse(
	val centralCell: CellResponse,
	val touchingCells: List<CellResponse>
)
