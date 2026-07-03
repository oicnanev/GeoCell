package org.sdato.geocell.controller.user

import org.sdato.geocell.domain.auth.AuthUserPrincipal
import org.sdato.geocell.dto.request.CreateUserRequest
import org.sdato.geocell.dto.response.CreateUserResponse
import org.sdato.geocell.exception.InvalidCredentialsException
import org.sdato.geocell.service.user.UserService
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
@Profile("!test")
class UserController(
	private val userService: UserService
) {
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	fun createUser(
		@RequestBody request: CreateUserRequest,
		authentication: Authentication
	): CreateUserResponse =
		userService.createUser(request, requirePrincipal(authentication))

	@GetMapping
	fun listUsers(authentication: Authentication): List<CreateUserResponse> =
		userService.listUsers(requirePrincipal(authentication))

	@GetMapping("/{id}")
	fun getUserById(
		@PathVariable id: Long,
		authentication: Authentication
	): CreateUserResponse =
		userService.getUserById(id, requirePrincipal(authentication))

	private fun requirePrincipal(authentication: Authentication): AuthUserPrincipal =
		authentication.principal as? AuthUserPrincipal ?: throw InvalidCredentialsException()
}
