package org.sdato.geocell.dto.response

data class DistrictResponse(
	val id: String,
	val district: String,
	val countryId: String
)

data class CountyResponse(
	val id: Long,
	val countyCode: String,
	val county: String,
	val districtId: String
)

data class CellsByAdministrativeAreaResponse(
	val districtId: String,
	val countyId: Long?,
	val caopPolygonGeoJson: String?,
	val cells: List<CellResponse>
)
