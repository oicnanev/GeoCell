package org.sdato.geocell.repository.cell

import org.sdato.geocell.domain.cell.CellBandWriteRecord
import org.sdato.geocell.domain.cell.CellDetailsRecord
import org.sdato.geocell.domain.cell.CellLocationWriteRecord
import org.sdato.geocell.domain.cell.CellMccMncWriteRecord
import org.sdato.geocell.domain.cell.CellWriteRecord

interface CellRepository {
	fun findByCgi(cgi: String): List<CellDetailsRecord>
	fun findById(id: Long): CellDetailsRecord?
	fun insertLocation(record: CellLocationWriteRecord): Long
	fun updateLocation(id: Long, record: CellLocationWriteRecord)
	fun insertBand(record: CellBandWriteRecord): Long
	fun updateBand(id: Long, record: CellBandWriteRecord)
	fun upsertMccMnc(record: CellMccMncWriteRecord): Long
	fun insertEnbGnb(enbGnb: Int, locationId: Long): Long
	fun updateEnbGnb(id: Long, enbGnb: Int, locationId: Long)
	fun insertCell(record: CellWriteRecord): Long
	fun updateCell(id: Long, record: CellWriteRecord): Int
	fun upsertCellPolygon(cellId: Long, polygonWkt: String, polygonShortWkt: String)
	fun deleteCell(id: Long): Int
}
