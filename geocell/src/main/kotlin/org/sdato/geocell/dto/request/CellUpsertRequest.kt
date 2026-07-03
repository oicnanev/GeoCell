package org.sdato.geocell.dto.request

data class CellUpsertRequest(
	val lacTac: String,
	val ci: String? = null,
	val eciNci: String? = null,
	val cgi: String? = null,
	val paragonCgi: String? = null,
	val technology: Int,
	val azimuth: Int,
	val name: String? = null,
	val location: CellLocationRequest,
	val enbGnb: CellEnbGnbRequest,
	val mccMnc: CellMccMncRequest,
	val band: CellBandRequest? = null
)

data class CellLocationRequest(
	val latitude: Double,
	val longitude: Double,
	val address: String? = null,
	val address1: String? = null,
	val zip4: Int,
	val zip3: Int,
	val postalDesignation: String? = null,
	val countyId: Long? = null
)

data class CellEnbGnbRequest(
	val enbGnb: Int
)

data class CellMccMncRequest(
	val mcc: Int,
	val mnc: Int,
	val type: String? = null,
	val brand: String? = null,
	val operatorName: String? = null,
	val status: String? = null,
	val bands: String? = null,
	val notes: String? = null,
	val countryId: String? = null
)

data class CellBandRequest(
	val band: String? = null,
	val bandwidth: Double? = null,
	val uplinkFreq: Double? = null,
	val downlinkFreq: Double? = null,
	val earfcn: Double? = null
)
