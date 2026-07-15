package org.sdato.geocell.repository.cell

import org.sdato.geocell.domain.cell.CellBandWriteRecord
import org.sdato.geocell.domain.cell.CellDetailsRecord
import org.sdato.geocell.domain.cell.CellLocationWriteRecord
import org.sdato.geocell.domain.cell.CellMccMncWriteRecord
import org.sdato.geocell.domain.cell.CellWriteRecord

interface CellRepository {
	fun findByCgi(cgi: String): List<CellDetailsRecord>
	fun findByIdentifiers(cgi: String?, paragonCgi: String?): CellDetailsRecord?
	fun findNearbyCells(
		latitude: Double,
		longitude: Double,
		radiusMeters: Double,
		mnc: Int?,
		technologies: Set<Int>?
	): List<CellDetailsRecord>
	fun findById(id: Long): CellDetailsRecord?
	fun findCountyId(districtId: String, countyId: String): Long?
	fun findMccMncId(mcc: Int, mnc: Int): Long?
	fun findLocationId(record: CellLocationWriteRecord): Long?
	fun findBandId(record: CellBandWriteRecord): Long?
	fun findEnbGnbId(enbGnb: Int, locationId: Long): Long?
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
