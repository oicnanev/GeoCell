package org.sdato.geocell.dto.response

import java.time.OffsetDateTime

data class CellResponse(
	val id: Long,
	val lacTac: String?,
	val ci: String?,
	val eciNci: String?,
	val cgi: String?,
	val paragonCgi: String?,
	val technology: Int,
	val azimuth: Int,
	val name: String?,
	val createdAt: OffsetDateTime?,
	val updatedAt: OffsetDateTime?,
	val location: CellLocationResponse?,
	val enbGnb: CellEnbGnbResponse?,
	val mccMnc: CellMccMncResponse?,
	val band: CellBandResponse?,
	val polygonGeoJson: String?,
	val polygonShortGeoJson: String?
)

data class CellLocationResponse(
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

data class CellEnbGnbResponse(
	val id: Long,
	val enbGnb: Int
)

data class CellMccMncResponse(
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

data class CellBandResponse(
	val id: Long,
	val band: String?,
	val bandwidth: Double?,
	val uplinkFreq: Double?,
	val downlinkFreq: Double?,
	val earfcn: Double?
)
