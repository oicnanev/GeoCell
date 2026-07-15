package org.sdato.geocell.validator

import org.sdato.geocell.dto.request.CellUpsertRequest
import org.sdato.geocell.exception.ValidationException
import org.springframework.stereotype.Component

@Component
class CellUpsertRequestValidator {

	private val cgiPattern = Regex("^\\d{3}-\\d{2,3}-\\d{1,11}$|^\\d{3}-\\d{2,3}-\\d{1,5}-\\d{1,11}$")
	private val technologies = setOf(2, 3, 4, 5, 10)

	fun validate(request: CellUpsertRequest) {
		if (request.technology !in technologies) {
			throw ValidationException("technology must be one of [2, 3, 4, 5, 10]")
		}
		if (request.azimuth !in 0..360) {
			throw ValidationException("azimuth must be between 0 and 360")
		}
		if (request.cgi.isNullOrBlank() && request.paragonCgi.isNullOrBlank()) {
			throw ValidationException("At least one of cgi or paragonCgi is required")
		}
		request.cgi?.let {
			if (!cgiPattern.matches(it)) {
				throw ValidationException("cgi format is invalid")
			}
		}
		request.location.let { location ->
			if (location.latitude !in -90.0..90.0) {
				throw ValidationException("location.latitude must be between -90 and 90")
			}
			if (location.longitude !in -180.0..180.0) {
				throw ValidationException("location.longitude must be between -180 and 180")
			}
		}
		request.mccMnc.let { mccmnc ->
			if (mccmnc.mcc !in 100..999) {
				throw ValidationException("mcc must be between 100 and 999")
			}
			if (mccmnc.mnc !in 0..999) {
				throw ValidationException("mnc must be between 0 and 999")
			}
		}
	}
}
