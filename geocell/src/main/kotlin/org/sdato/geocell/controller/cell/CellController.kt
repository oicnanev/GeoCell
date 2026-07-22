package org.sdato.geocell.controller.cell

import org.sdato.geocell.domain.auth.AuthUserPrincipal
import org.sdato.geocell.dto.request.CellUpsertRequest
import org.sdato.geocell.dto.response.CellCsvImportResponse
import org.sdato.geocell.dto.response.CellsByAdministrativeAreaResponse
import org.sdato.geocell.dto.response.NearbyCellsResponse
import org.sdato.geocell.dto.response.CellsInCircleResponse
import org.sdato.geocell.dto.response.CellsInBboxResponse
import org.sdato.geocell.dto.response.CellResponse
import org.sdato.geocell.dto.response.CountyResponse
import org.sdato.geocell.dto.response.DistrictResponse
import org.sdato.geocell.dto.response.LacTacCoverageResponse
import org.sdato.geocell.exception.InvalidCredentialsException
import org.sdato.geocell.service.cell.CellService
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/cells")
@Profile("!test")
class CellController(
	private val cellService: CellService
) {

	@GetMapping
	fun getByCgi(@RequestParam cgi: String): List<CellResponse> =
		cellService.getCellsByCgi(cgi)

	@GetMapping("/districts")
	fun getDistrictsByCountry(@RequestParam country: String): List<DistrictResponse> =
		cellService.getDistrictsByCountry(country)

	@GetMapping("/counties")
	fun getCountiesByDistrict(@RequestParam districtId: String): List<CountyResponse> =
		cellService.getCountiesByDistrict(districtId)

	@GetMapping("/nearby")
	fun getNearbyCells(
		@RequestParam cgi: String,
		@RequestParam radiusKm: Double,
		@RequestParam(required = false) sameNetwork: String?,
		@RequestParam(required = false) techGeneration: List<String>?
	): NearbyCellsResponse =
		cellService.getNearbyCells(cgi, radiusKm, sameNetwork, techGeneration)

	@GetMapping("/search/circle")
	fun getCellsInCircle(
		@RequestParam lat: Double,
		@RequestParam lon: Double,
		@RequestParam radiusKm: Double,
		@RequestParam(required = false) mnc: Int?,
		@RequestParam(required = false) band: String?,
		@RequestParam(required = false) techGeneration: List<String>?
	): CellsInCircleResponse =
		cellService.getCellsInCircle(lat, lon, radiusKm, mnc, band, techGeneration)

	@GetMapping("/search/bbox")
	fun getCellsInBbox(
		@RequestParam lat1: Double,
		@RequestParam lon1: Double,
		@RequestParam lat2: Double,
		@RequestParam lon2: Double,
		@RequestParam(required = false) mnc: Int?,
		@RequestParam(required = false) band: String?,
		@RequestParam(required = false) techGeneration: List<String>?
	): CellsInBboxResponse =
		cellService.getCellsInBbox(lat1, lon1, lat2, lon2, mnc, band, techGeneration)

	@GetMapping("/search/county")
	fun getCellsByAdministrativeArea(
		@RequestParam districtId: String,
		@RequestParam(required = false) countyId: Long?,
		@RequestParam(required = false) mnc: Int?,
		@RequestParam(required = false) techGeneration: List<String>?
	): CellsByAdministrativeAreaResponse =
		cellService.getCellsByAdministrativeArea(districtId, countyId, mnc, techGeneration)

	@GetMapping("/search/lac-tac")
	fun getCellsByLacTac(
		@RequestParam mcc: Int,
		@RequestParam mnc: Int,
		@RequestParam lacTac: String
	): List<CellResponse> =
		cellService.getCellsByLacTac(mcc, mnc, lacTac)

	@GetMapping("/search/lac-tac/polygon")
	fun getLacTacCoveragePolygon(
		@RequestParam mcc: Int,
		@RequestParam mnc: Int,
		@RequestParam lacTac: String
	): LacTacCoverageResponse =
		cellService.getLacTacCoveragePolygon(mcc, mnc, lacTac)

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	fun createCell(
		@RequestBody request: CellUpsertRequest,
		authentication: Authentication
	): CellResponse =
		cellService.createCell(request, requirePrincipal(authentication))

	@PostMapping("/import", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun importCellsCsv(
		@RequestParam("file") file: MultipartFile,
		authentication: Authentication
	): CellCsvImportResponse =
		cellService.importCellsCsv(file, requirePrincipal(authentication))

	@PutMapping("/{id}")
	fun updateCell(
		@PathVariable id: Long,
		@RequestBody request: CellUpsertRequest,
		authentication: Authentication
	): CellResponse =
		cellService.updateCell(id, request, requirePrincipal(authentication))

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun deleteCell(@PathVariable id: Long) {
		cellService.deleteCell(id)
	}

	private fun requirePrincipal(authentication: Authentication): AuthUserPrincipal =
		authentication.principal as? AuthUserPrincipal ?: throw InvalidCredentialsException()
}
