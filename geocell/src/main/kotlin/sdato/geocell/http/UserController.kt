package sdato.geocell.http

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sdato.geocell.config.ApiRoutes
import sdato.geocell.dto.request.CreateUserRequest
import sdato.geocell.dto.response.UserResponse
import sdato.geocell.service.UserService

@RestController
@RequestMapping(ApiRoutes.USER_BASE)
class UserController(private val userService: UserService) {
    @PostMapping
    fun createUser(
        @Valid @RequestBody request: CreateUserRequest,
    ): UserResponse {
        return userService.createUser(request)
    }

    @GetMapping("/{id}")
    fun getUser(
        @PathVariable id: Long,
    ): UserResponse {
        return userService.getUserById(id)
    }

    @DeleteMapping("/{id}")
    fun deactivateUser(
        @PathVariable id: Long,
    ) {
        userService.deactivateUser(id)
    }
}
