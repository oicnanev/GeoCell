package org.sdato.geocell.dto.response

data class CellCsvImportResponse(
	val rowsProcessed: Int,
	val inserted: Int,
	val updated: Int,
	val polygonsUpserted: Int
)
