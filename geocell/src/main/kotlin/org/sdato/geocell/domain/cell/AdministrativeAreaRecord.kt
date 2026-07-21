package org.sdato.geocell.domain.cell

data class DistrictRecord(
	val id: String,
	val district: String,
	val countryId: String
)

data class CountyRecord(
	val id: Long,
	val countyCode: String,
	val county: String,
	val districtId: String
)
