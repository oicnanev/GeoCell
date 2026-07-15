package org.sdato.geocell.service.cell

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.sdato.geocell.domain.auth.AuthUserPrincipal
import org.sdato.geocell.domain.cell.CellBandWriteRecord
import org.sdato.geocell.domain.cell.CellDetailsRecord
import org.sdato.geocell.domain.cell.CellLocationWriteRecord
import org.sdato.geocell.domain.cell.CellMccMncWriteRecord
import org.sdato.geocell.domain.cell.CellWriteRecord
import org.sdato.geocell.dto.request.CellUpsertRequest
import org.sdato.geocell.dto.response.CellCsvImportResponse
import org.sdato.geocell.dto.response.CellBandResponse
import org.sdato.geocell.dto.response.CellEnbGnbResponse
import org.sdato.geocell.dto.response.CellLocationResponse
import org.sdato.geocell.dto.response.CellMccMncResponse
import org.sdato.geocell.dto.response.CellResponse
import org.sdato.geocell.dto.response.NearbyCellsResponse
import org.sdato.geocell.exception.ResourceNotFoundException
import org.sdato.geocell.exception.ValidationException
import org.sdato.geocell.repository.cell.CellRepository
import org.sdato.geocell.validator.CellUpsertRequestValidator
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.StringReader
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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
			throw ValidationException("cgi query parameter is required")
		}
		val cells = cellRepository.findByCgi(normalizedCgi)
		if (cells.isEmpty()) {
			throw ResourceNotFoundException("No cells found for cgi '$normalizedCgi'")
		}
		return cells.map { it.toResponse() }
	}

	fun getNearbyCells(
		cgi: String,
		radiusKm: Double,
		sameNetwork: String?,
		techGenerations: List<String>?
	): NearbyCellsResponse {
		val normalizedCgi = cgi.trim()
		if (normalizedCgi.isBlank()) {
			throw ValidationException("cgi query parameter is required")
		}
		if (radiusKm <= 0.0) {
			throw ValidationException("radiusKm must be greater than 0")
		}

		val center = cellRepository.findByCgi(normalizedCgi).firstOrNull()
			?: throw ResourceNotFoundException("No cells found for cgi '$normalizedCgi'")
		val latitude = center.location?.latitude
			?: throw ValidationException("Central cell does not have location coordinates")
		val longitude = center.location.longitude
			?: throw ValidationException("Central cell does not have location coordinates")

		val sameNetworkEnabled = parseSameNetworkFlag(sameNetwork)
		val mncFilter = if (sameNetworkEnabled) {
			center.mccMnc?.mnc ?: throw ValidationException("Central cell does not have MNC information")
		} else {
			null
		}
		val technologies = parseTechnologyGenerations(techGenerations)
		val nearby = cellRepository.findNearbyCells(
			latitude = latitude,
			longitude = longitude,
			radiusMeters = radiusKm * 1000.0,
			mnc = mncFilter,
			technologies = technologies
		)
		val centralCell = nearby.firstOrNull { it.id == center.id } ?: center
		return NearbyCellsResponse(
			centralCell = centralCell.toResponse(includePolygon = true, includePolygonShort = false),
			cellsInRadius = nearby
				.filter { it.id != center.id }
				.map { it.toResponse(includePolygon = false, includePolygonShort = true) }
		)
	}

	@Transactional
	fun importCellsCsv(file: MultipartFile, principal: AuthUserPrincipal): CellCsvImportResponse {
		validateCsvFile(file)
		val rows = parseCsvRows(file)
		if (rows.isEmpty()) {
			throw ValidationException("CSV file must contain at least one data row")
		}

		var inserted = 0
		var updated = 0
		var polygonsUpserted = 0

		rows.forEach { row ->
			val existing = cellRepository.findByIdentifiers(row.cgi, row.paragonCgi)
			val mccMncId = cellRepository.findMccMncId(row.mcc, row.mnc)
				?: cellRepository.upsertMccMnc(
					CellMccMncWriteRecord(
						type = null,
						mcc = row.mcc,
						mnc = row.mnc,
						brand = null,
						operatorName = null,
						status = null,
						bands = null,
						notes = null,
						countryId = null
					)
				)
			val locationRecord = CellLocationWriteRecord(
				latitude = row.latitude,
				longitude = row.longitude,
				address = row.address,
				address1 = row.address1,
				zip4 = row.zip4,
				zip3 = row.zip3,
				postalDesignation = row.postalDesignation,
				countyId = row.countyId
			)
			val bandRecord = CellBandWriteRecord(
				band = row.band,
				bandwidth = null,
				uplinkFreq = null,
				downlinkFreq = null,
				earfcn = null
			)
			val rowTimestamp = row.date.atStartOfDay().atOffset(ZoneOffset.UTC)
			val locationId = resolveOrCreateLocationId(locationRecord)
			val bandId = resolveOrCreateBandId(bandRecord)
			val enbGnbId = resolveOrCreateEnbGnbId(row.enbGnb, locationId)

			if (existing == null) {
				val cellId = cellRepository.insertCell(
					CellWriteRecord(
						lacTac = row.lacTac,
						ci = row.ci,
						eciNci = row.eciNci,
						cgi = row.cgi,
						paragonCgi = row.paragonCgi,
						technology = row.technology,
						azimuth = row.azimuth,
						name = row.name,
						bandId = bandId,
						enbGnbId = enbGnbId,
						locationId = locationId,
						mccMncId = mccMncId,
						updatedBy = principal.userId,
						createdBy = principal.userId,
						createdAt = rowTimestamp,
						updatedAt = rowTimestamp
					)
				)
				upsertPolygons(cellId, row.latitude, row.longitude, row.azimuth, row.technology)
				inserted += 1
				polygonsUpserted += 1
				return@forEach
			}

			cellRepository.updateCell(
				id = existing.id,
				record = CellWriteRecord(
					lacTac = row.lacTac,
					ci = row.ci,
					eciNci = row.eciNci,
					cgi = row.cgi,
					paragonCgi = row.paragonCgi,
					technology = row.technology,
					azimuth = row.azimuth,
					name = row.name,
					bandId = bandId,
					enbGnbId = enbGnbId,
					locationId = locationId,
					mccMncId = mccMncId,
					updatedBy = principal.userId,
					updatedAt = rowTimestamp
				)
			)

			if (existing.azimuth != row.azimuth) {
				upsertPolygons(existing.id, row.latitude, row.longitude, row.azimuth, row.technology)
				polygonsUpserted += 1
			}
			updated += 1
		}

		return CellCsvImportResponse(
			rowsProcessed = rows.size,
			inserted = inserted,
			updated = updated,
			polygonsUpserted = polygonsUpserted
		)
	}

	@Transactional
	fun createCell(request: CellUpsertRequest, principal: AuthUserPrincipal): CellResponse {
		cellUpsertRequestValidator.validate(request)

		val locationId = resolveOrCreateLocationId(
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
			resolveOrCreateBandId(
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

		val enbGnbId = resolveOrCreateEnbGnbId(request.enbGnb.enbGnb, locationId)
		val normalizedCgi = request.cgi?.trim()
		val normalizedParagonCgi = request.paragonCgi?.trim()

		val cellId = cellRepository.insertCell(
			CellWriteRecord(
				lacTac = request.lacTac?.trim()?.ifBlank { null },
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
		cellRepository.findById(id) ?: throw ResourceNotFoundException("Cell with id $id was not found")

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
		val locationId = resolveOrCreateLocationId(locationRecord)

		val bandId = request.band?.let { bandRequest ->
			val bandRecord = CellBandWriteRecord(
				band = bandRequest.band,
				bandwidth = bandRequest.bandwidth,
				uplinkFreq = bandRequest.uplinkFreq,
				downlinkFreq = bandRequest.downlinkFreq,
				earfcn = bandRequest.earfcn
			)
			resolveOrCreateBandId(bandRecord)
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

		val enbGnbId = resolveOrCreateEnbGnbId(request.enbGnb.enbGnb, locationId)

		val updatedRows = cellRepository.updateCell(
			id = id,
			record = CellWriteRecord(
				lacTac = request.lacTac?.trim()?.ifBlank { null },
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

	private fun resolveOrCreateLocationId(record: CellLocationWriteRecord): Long =
		cellRepository.findLocationId(record) ?: cellRepository.insertLocation(record)

	private fun resolveOrCreateBandId(record: CellBandWriteRecord): Long =
		cellRepository.findBandId(record) ?: cellRepository.insertBand(record)

	private fun resolveOrCreateEnbGnbId(enbGnb: Int, locationId: Long): Long =
		cellRepository.findEnbGnbId(enbGnb, locationId) ?: cellRepository.insertEnbGnb(enbGnb, locationId)

	private fun validateCsvFile(file: MultipartFile) {
		if (file.isEmpty) {
			throw ValidationException("CSV file is required")
		}
		val filename = file.originalFilename?.trim().orEmpty()
		if (!filename.lowercase().endsWith(".csv")) {
			throw ValidationException("Only .csv files are supported")
		}
	}

	private fun parseCsvRows(file: MultipartFile): List<CsvCellRow> {
		val content = file.bytes.toString(StandardCharsets.UTF_8)
		val headerLine = content.lineSequence().firstOrNull { it.isNotBlank() }
			?: throw ValidationException("CSV file is empty")
		val delimiter = detectDelimiter(headerLine)
		val format = CSVFormat.DEFAULT.builder()
			.setDelimiter(delimiter)
			.setHeader()
			.setSkipHeaderRecord(true)
			.setIgnoreHeaderCase(true)
			.setTrim(true)
			.build()

		CSVParser(StringReader(content), format).use { parser ->
			val normalizedHeaders = parser.headerMap.keys.associateBy { normalizeHeader(it) }
			return parser.records.map { record -> record.toCsvRow(normalizedHeaders) }
		}
	}

	private fun CSVRecord.toCsvRow(headers: Map<String, String>): CsvCellRow {
		val rowNumber = recordNumber.toInt() + 1
		val mcc = requiredInt(headers, "MCC", rowNumber)
		val mnc = requiredInt(headers, "MNC", rowNumber)
		val technology = requiredInt(headers, "TECNOLOGIA", rowNumber)
		if (technology !in allowedTechnologies) {
			throw ValidationException("Row $rowNumber: TECNOLOGIA must be one of ${allowedTechnologies.joinToString(", ")}")
		}
		val azimuth = requiredInt(headers, "AZIMUTE", rowNumber)
		if (azimuth !in 0..360) {
			throw ValidationException("Row $rowNumber: AZIMUTE must be between 0 and 360")
		}
		val (cgi, paragonCgi) = normalizeIdentifiers(
			rawCgi = optionalValue(headers, "CGI_ECGI_NCGI"),
			rawParagonCgi = optionalValue(headers, "PARAGON_ID"),
			rowNumber = rowNumber
		)
		val countyId = resolveCountyId(optionalValue(headers, "CONCELHO_CD"))

		return CsvCellRow(
			mcc = mcc,
			mnc = mnc,
			lacTac = optionalValue(headers, "LAC_TAC"),
			ci = optionalValue(headers, "CI"),
			eciNci = optionalValue(headers, "ECI_NCI"),
			cgi = cgi,
			paragonCgi = paragonCgi,
			technology = technology,
			band = optionalValue(headers, "BANDA"),
			latitude = requiredDouble(headers, "LATITUDE", rowNumber),
			longitude = requiredDouble(headers, "LONGITUDE", rowNumber),
			azimuth = azimuth,
			name = optionalValue(headers, "NOME"),
			address = optionalValue(headers, "MORADA"),
			address1 = optionalValue(headers, "MORADA1"),
			zip4 = optionalInt(headers, "CP4", rowNumber) ?: 0,
			zip3 = optionalInt(headers, "CP3", rowNumber) ?: 0,
			postalDesignation = optionalValue(headers, "DESIGNACAO_POSTAL"),
			date = requiredDate(headers, rowNumber, "DATA"),
			enbGnb = optionalInt(headers, "ENB_GNB", rowNumber) ?: 0,
			countyId = countyId
		)
	}

	private fun normalizeIdentifiers(rawCgi: String?, rawParagonCgi: String?, rowNumber: Int): Pair<String?, String?> {
		val normalizedCgi = rawCgi?.trim()?.takeIf { it.isNotBlank() }
		val normalizedParagon = rawParagonCgi?.trim()?.takeIf { it.isNotBlank() }
		if (normalizedCgi == null && normalizedParagon == null) {
			throw ValidationException("Row $rowNumber: at least one of CGI_ECGI_NCGI or PARAGON_ID is required")
		}

		val cgiValid = normalizedCgi?.let { cgiPattern.matches(it) } ?: false
		val paragonValid = normalizedParagon?.let { cgiPattern.matches(it) } ?: false
		if (!cgiValid && !paragonValid) {
			throw ValidationException(
				"Row $rowNumber: neither CGI_ECGI_NCGI nor PARAGON_ID has a supported CGI format"
			)
		}

		// Prefer CGI_ECGI_NCGI when valid; otherwise persist PARAGON_ID as CGI fallback.
		val cgiToPersist = when {
			cgiValid -> normalizedCgi
			paragonValid -> normalizedParagon
			else -> null
		}
		return cgiToPersist to normalizedParagon
	}

	private fun resolveCountyId(concelhoCode: String?): Long? {
		if (concelhoCode.isNullOrBlank()) {
			return null
		}
		val digitsOnly = concelhoCode.filter { it.isDigit() }
		if (digitsOnly.length != 4) {
			return null
		}
		val districtId = digitsOnly.substring(0, 2)
		val countyId = digitsOnly.substring(2, 4)
		return cellRepository.findCountyId(districtId, countyId)
	}

	private fun CSVRecord.requiredValue(headers: Map<String, String>, name: String, rowNumber: Int): String {
		val value = optionalValue(headers, name)
		if (value == null) {
			throw ValidationException("Row $rowNumber: $name is required")
		}
		return value
	}

	private fun CSVRecord.optionalValue(headers: Map<String, String>, vararg names: String): String? {
		val key = names.asSequence()
			.map { normalizeHeader(it) }
			.mapNotNull { headers[it] }
			.firstOrNull() ?: return null
		val value = get(key)?.trim().orEmpty()
		return value.ifBlank { null }
	}

	private fun CSVRecord.requiredInt(headers: Map<String, String>, name: String, rowNumber: Int): Int {
		val value = requiredValue(headers, name, rowNumber)
		return value.toIntOrNull() ?: throw ValidationException("Row $rowNumber: $name must be an integer")
	}

	private fun CSVRecord.optionalInt(headers: Map<String, String>, name: String, rowNumber: Int): Int? {
		val value = optionalValue(headers, name) ?: return null
		return value.toIntOrNull() ?: throw ValidationException("Row $rowNumber: $name must be an integer")
	}

	private fun CSVRecord.requiredDouble(headers: Map<String, String>, name: String, rowNumber: Int): Double {
		val value = requiredValue(headers, name, rowNumber)
		return value.toDoubleOrNull() ?: throw ValidationException("Row $rowNumber: $name must be a decimal number")
	}

	private fun CSVRecord.requiredDate(headers: Map<String, String>, rowNumber: Int, name: String): LocalDate {
		val rawDate = requiredValue(headers, name, rowNumber)
		dateFormats.forEach { formatter ->
			try {
				return LocalDate.parse(rawDate, formatter)
			} catch (_: DateTimeParseException) {
				// try next format
			}
		}
		throw ValidationException("Row $rowNumber: DATA must match one of ${dateFormatsDescription.joinToString(", ")}")
	}

	private fun normalizeHeader(header: String): String =
		header.replace("\uFEFF", "").trim().uppercase()

	private fun detectDelimiter(headerLine: String): Char {
		val semicolonCount = headerLine.count { it == ';' }
		val commaCount = headerLine.count { it == ',' }
		return if (semicolonCount >= commaCount) ';' else ','
	}

	private fun parseSameNetworkFlag(value: String?): Boolean {
		val normalized = value?.trim()?.lowercase() ?: return false
		return when (normalized) {
			"1", "true" -> true
			"0", "false" -> false
			else -> throw ValidationException("sameNetwork must be one of: 1, 0, true, false")
		}
	}

	private fun parseTechnologyGenerations(values: List<String>?): Set<Int>? {
		if (values.isNullOrEmpty()) {
			return null
		}
		val parsed = values
			.asSequence()
			.flatMap { it.split(",").asSequence() }
			.map { it.trim() }
			.filter { it.isNotBlank() }
			.map { generation ->
				when (generation.uppercase().replace("_", "-")) {
					"2G" -> 2
					"3G" -> 3
					"4G" -> 4
					"5G" -> 5
					"NB-IOT", "NBIOT" -> 10
					else -> throw ValidationException("techGeneration contains unsupported value '$generation'")
				}
			}
			.toSet()
		return parsed.ifEmpty { null }
	}

	private fun CellDetailsRecord.toResponse(
		includePolygon: Boolean = true,
		includePolygonShort: Boolean = true
	): CellResponse =
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
			polygonGeoJson = if (includePolygon) polygonGeoJson else null,
			polygonShortGeoJson = if (includePolygonShort) polygonShortGeoJson else null
		)

	private data class CsvCellRow(
		val mcc: Int,
		val mnc: Int,
		val lacTac: String?,
		val ci: String?,
		val eciNci: String?,
		val cgi: String?,
		val paragonCgi: String?,
		val technology: Int,
		val band: String?,
		val latitude: Double,
		val longitude: Double,
		val azimuth: Int,
		val name: String?,
		val address: String?,
		val address1: String?,
		val zip4: Int,
		val zip3: Int,
		val postalDesignation: String?,
		val date: LocalDate,
		val enbGnb: Int,
		val countyId: Long?
	)

	companion object {
		private val allowedTechnologies = setOf(2, 3, 4, 5, 10)
		private val cgiPattern = Regex("^\\d{3}-\\d{2,3}-\\d{1,11}$|^\\d{3}-\\d{2,3}-\\d{1,5}-\\d{1,11}$")
		private val dateFormats = listOf(
			DateTimeFormatter.ISO_LOCAL_DATE,
			DateTimeFormatter.ofPattern("d/M/yyyy"),
			DateTimeFormatter.ofPattern("d-M-yyyy")
		)
		private val dateFormatsDescription = listOf("yyyy-MM-dd", "d/M/yyyy", "d-M-yyyy")
	}
}
