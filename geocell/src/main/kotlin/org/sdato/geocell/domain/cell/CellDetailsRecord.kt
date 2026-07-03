package org.sdato.geocell.domain.cell

import java.time.OffsetDateTime

data class CellDetailsRecord(
	val id: Long,
	val lacTac: String,
	val ci: String?,
	val eciNci: String?,
	val cgi: String?,
	val paragonCgi: String?,
	val technology: Int,
	val azimuth: Int,
	val name: String?,
	val createdAt: OffsetDateTime?,
	val updatedAt: OffsetDateTime?,
	val location: CellLocationRecord?,
	val enbGnb: CellEnbGnbRecord?,
	val mccMnc: CellMccMncRecord?,
	val band: CellBandRecord?,
	val polygonGeoJson: String?,
	val polygonShortGeoJson: String?
)

data class CellLocationRecord(
	val id: Long,
	val latitude: Double?,
	val longitude: Double?,
	val address: String?,
	val address1: String?,
	val zip4: Int?,
	val zip3: Int?,
	val postalDesignation: String?,
	val countyId: Long?
)

data class CellEnbGnbRecord(
	val id: Long,
	val enbGnb: Int
)

data class CellMccMncRecord(
	val id: Long,
	val type: String?,
	val mcc: Int,
	val mnc: Int,
	val brand: String?,
	val operatorName: String?,
	val status: String?,
	val bands: String?,
	val notes: String?,
	val countryId: String?
)

data class CellBandRecord(
	val id: Long,
	val band: String?,
	val bandwidth: Double?,
	val uplinkFreq: Double?,
	val downlinkFreq: Double?,
	val earfcn: Double?
)

data class CellLocationWriteRecord(
	val latitude: Double,
	val longitude: Double,
	val address: String?,
	val address1: String?,
	val zip4: Int,
	val zip3: Int,
	val postalDesignation: String?,
	val countyId: Long?
)

data class CellBandWriteRecord(
	val band: String?,
	val bandwidth: Double?,
	val uplinkFreq: Double?,
	val downlinkFreq: Double?,
	val earfcn: Double?
)

data class CellMccMncWriteRecord(
	val type: String?,
	val mcc: Int,
	val mnc: Int,
	val brand: String?,
	val operatorName: String?,
	val status: String?,
	val bands: String?,
	val notes: String?,
	val countryId: String?
)

data class CellWriteRecord(
	val lacTac: String,
	val ci: String?,
	val eciNci: String?,
	val cgi: String?,
	val paragonCgi: String?,
	val technology: Int,
	val azimuth: Int,
	val name: String?,
	val bandId: Long?,
	val enbGnbId: Long?,
	val locationId: Long?,
	val mccMncId: Long?,
	val updatedBy: Long,
	val createdBy: Long? = null
)
