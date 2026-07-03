package org.sdato.geocell.controller.cell

import org.sdato.geocell.domain.auth.AuthUserPrincipal
import org.sdato.geocell.dto.request.CellUpsertRequest
import org.sdato.geocell.dto.response.CellResponse
import org.sdato.geocell.exception.InvalidCredentialsException
import org.sdato.geocell.service.cell.CellService
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
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

@RestController
@RequestMapping("/api/cells")
@Profile("!test")
class CellController(
	private val cellService: CellService
) {

	@GetMapping
	fun getByCgi(@RequestParam cgi: String): List<CellResponse> =
		cellService.getCellsByCgi(cgi)

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	fun createCell(
		@RequestBody request: CellUpsertRequest,
		authentication: Authentication
	): CellResponse =
		cellService.createCell(request, requirePrincipal(authentication))

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
