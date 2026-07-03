package org.sdato.geocell.service.cell

import org.sdato.geocell.domain.auth.AuthUserPrincipal
import org.sdato.geocell.domain.cell.CellBandWriteRecord
import org.sdato.geocell.domain.cell.CellDetailsRecord
import org.sdato.geocell.domain.cell.CellLocationWriteRecord
import org.sdato.geocell.domain.cell.CellMccMncWriteRecord
import org.sdato.geocell.domain.cell.CellWriteRecord
import org.sdato.geocell.dto.request.CellUpsertRequest
import org.sdato.geocell.dto.response.CellBandResponse
import org.sdato.geocell.dto.response.CellEnbGnbResponse
import org.sdato.geocell.dto.response.CellLocationResponse
import org.sdato.geocell.dto.response.CellMccMncResponse
import org.sdato.geocell.dto.response.CellResponse
import org.sdato.geocell.exception.ResourceNotFoundException
import org.sdato.geocell.repository.cell.CellRepository
import org.sdato.geocell.validator.CellUpsertRequestValidator
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Profile("!test")
class CellService(
	private val cellRepository: CellRepository,
	private val cellUpsertRequestValidator: CellUpsertRequestValidator,
	private val cellPolygonGenerator: CellPolygonGenerator
) {

	fun getCellsByCgi(cgi: String): List<CellResponse> {
		val normalizedCgi = cgi.trim()
		if (normalizedCgi.isBlank()) {
			throw org.sdato.geocell.exception.ValidationException("cgi query parameter is required")
		}
		val cells = cellRepository.findByCgi(normalizedCgi)
		if (cells.isEmpty()) {
			throw ResourceNotFoundException("No cells found for cgi '$normalizedCgi'")
		}
		return cells.map { it.toResponse() }
	}

	@Transactional
	fun createCell(request: CellUpsertRequest, principal: AuthUserPrincipal): CellResponse {
		cellUpsertRequestValidator.validate(request)

		val locationId = cellRepository.insertLocation(
			CellLocationWriteRecord(
				latitude = request.location.latitude,
				longitude = request.location.longitude,
				address = request.location.address,
				address1 = request.location.address1,
				zip4 = request.location.zip4,
				zip3 = request.location.zip3,
				postalDesignation = request.location.postalDesignation,
				countyId = request.location.countyId
			)
		)

		val bandId = request.band?.let {
			cellRepository.insertBand(
				CellBandWriteRecord(
					band = it.band,
					bandwidth = it.bandwidth,
					uplinkFreq = it.uplinkFreq,
					downlinkFreq = it.downlinkFreq,
					earfcn = it.earfcn
				)
			)
		}

		val mccMncId = cellRepository.upsertMccMnc(
			CellMccMncWriteRecord(
				type = request.mccMnc.type,
				mcc = request.mccMnc.mcc,
				mnc = request.mccMnc.mnc,
				brand = request.mccMnc.brand,
				operatorName = request.mccMnc.operatorName,
				status = request.mccMnc.status,
				bands = request.mccMnc.bands,
				notes = request.mccMnc.notes,
				countryId = request.mccMnc.countryId
			)
		)

		val enbGnbId = cellRepository.insertEnbGnb(request.enbGnb.enbGnb, locationId)
		val normalizedCgi = request.cgi?.trim()
		val normalizedParagonCgi = request.paragonCgi?.trim()

		val cellId = cellRepository.insertCell(
			CellWriteRecord(
				lacTac = request.lacTac.trim(),
				ci = request.ci?.trim(),
				eciNci = request.eciNci?.trim(),
				cgi = normalizedCgi,
				paragonCgi = normalizedParagonCgi,
				technology = request.technology,
				azimuth = request.azimuth,
				name = request.name?.trim(),
				bandId = bandId,
				enbGnbId = enbGnbId,
				locationId = locationId,
				mccMncId = mccMncId,
				updatedBy = principal.userId,
				createdBy = principal.userId
			)
		)

		upsertPolygons(
			cellId = cellId,
			latitude = request.location.latitude,
			longitude = request.location.longitude,
			direction = request.azimuth,
			technology = request.technology
		)

		return cellRepository.findById(cellId)?.toResponse()
			?: throw IllegalStateException("Created cell $cellId could not be loaded")
	}

	@Transactional
	fun updateCell(id: Long, request: CellUpsertRequest, principal: AuthUserPrincipal): CellResponse {
		cellUpsertRequestValidator.validate(request)
		val current = cellRepository.findById(id) ?: throw ResourceNotFoundException("Cell with id $id was not found")

		val locationRecord = CellLocationWriteRecord(
			latitude = request.location.latitude,
			longitude = request.location.longitude,
			address = request.location.address,
			address1 = request.location.address1,
			zip4 = request.location.zip4,
			zip3 = request.location.zip3,
			postalDesignation = request.location.postalDesignation,
			countyId = request.location.countyId
		)
		val locationId = current.location?.id?.also { cellRepository.updateLocation(it, locationRecord) }
			?: cellRepository.insertLocation(locationRecord)

		val bandId = request.band?.let { bandRequest ->
			val bandRecord = CellBandWriteRecord(
				band = bandRequest.band,
				bandwidth = bandRequest.bandwidth,
				uplinkFreq = bandRequest.uplinkFreq,
				downlinkFreq = bandRequest.downlinkFreq,
				earfcn = bandRequest.earfcn
			)
			current.band?.id?.also { cellRepository.updateBand(it, bandRecord) }
				?: cellRepository.insertBand(bandRecord)
		}

		val mccMncId = cellRepository.upsertMccMnc(
			CellMccMncWriteRecord(
				type = request.mccMnc.type,
				mcc = request.mccMnc.mcc,
				mnc = request.mccMnc.mnc,
				brand = request.mccMnc.brand,
				operatorName = request.mccMnc.operatorName,
				status = request.mccMnc.status,
				bands = request.mccMnc.bands,
				notes = request.mccMnc.notes,
				countryId = request.mccMnc.countryId
			)
		)

		val enbGnbId = current.enbGnb?.id?.also {
			cellRepository.updateEnbGnb(it, request.enbGnb.enbGnb, locationId)
		} ?: cellRepository.insertEnbGnb(request.enbGnb.enbGnb, locationId)

		val updatedRows = cellRepository.updateCell(
			id = id,
			record = CellWriteRecord(
				lacTac = request.lacTac.trim(),
				ci = request.ci?.trim(),
				eciNci = request.eciNci?.trim(),
				cgi = request.cgi?.trim(),
				paragonCgi = request.paragonCgi?.trim(),
				technology = request.technology,
				azimuth = request.azimuth,
				name = request.name?.trim(),
				bandId = bandId,
				enbGnbId = enbGnbId,
				locationId = locationId,
				mccMncId = mccMncId,
				updatedBy = principal.userId
			)
		)
		if (updatedRows == 0) {
			throw ResourceNotFoundException("Cell with id $id was not found")
		}

		upsertPolygons(
			cellId = id,
			latitude = request.location.latitude,
			longitude = request.location.longitude,
			direction = request.azimuth,
			technology = request.technology
		)

		return cellRepository.findById(id)?.toResponse()
			?: throw IllegalStateException("Updated cell $id could not be loaded")
	}

	@Transactional
	fun deleteCell(id: Long) {
		val deletedRows = cellRepository.deleteCell(id)
		if (deletedRows == 0) {
			throw ResourceNotFoundException("Cell with id $id was not found")
		}
	}

	private fun upsertPolygons(cellId: Long, latitude: Double, longitude: Double, direction: Int, technology: Int) {
		val polygonWkt = cellPolygonGenerator.buildDefaultPolygonWkt(latitude, longitude, direction)
		val polygonShortWkt = cellPolygonGenerator.buildShortPolygonWkt(latitude, longitude, direction, technology)
		cellRepository.upsertCellPolygon(cellId, polygonWkt, polygonShortWkt)
	}

	private fun CellDetailsRecord.toResponse(): CellResponse =
		CellResponse(
			id = id,
			lacTac = lacTac,
			ci = ci,
			eciNci = eciNci,
			cgi = cgi,
			paragonCgi = paragonCgi,
			technology = technology,
			azimuth = azimuth,
			name = name,
			createdAt = createdAt,
			updatedAt = updatedAt,
			location = location?.let {
				CellLocationResponse(
					id = it.id,
					latitude = it.latitude,
					longitude = it.longitude,
					address = it.address,
					address1 = it.address1,
					zip4 = it.zip4,
					zip3 = it.zip3,
					postalDesignation = it.postalDesignation,
					countyId = it.countyId
				)
			},
			enbGnb = enbGnb?.let {
				CellEnbGnbResponse(
					id = it.id,
					enbGnb = it.enbGnb
				)
			},
			mccMnc = mccMnc?.let {
				CellMccMncResponse(
					id = it.id,
					type = it.type,
					mcc = it.mcc,
					mnc = it.mnc,
					brand = it.brand,
					operatorName = it.operatorName,
					status = it.status,
					bands = it.bands,
					notes = it.notes,
					countryId = it.countryId
				)
			},
			band = band?.let {
				CellBandResponse(
					id = it.id,
					band = it.band,
					bandwidth = it.bandwidth,
					uplinkFreq = it.uplinkFreq,
					downlinkFreq = it.downlinkFreq,
					earfcn = it.earfcn
				)
			},
			polygonGeoJson = polygonGeoJson,
			polygonShortGeoJson = polygonShortGeoJson
		)
}
