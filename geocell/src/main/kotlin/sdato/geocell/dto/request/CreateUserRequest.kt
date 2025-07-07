package sdato.geocell.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import sdato.geocell.validation.ValidEmail

data class CreateUserRequest(
    @field:NotBlank(message = "Username cannot be blank")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String,
    @field:NotBlank(message = "Password cannot be blank")
    @field:Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    val password: String,
    @field:NotBlank(message = "Email cannot be blank")
    @field:ValidEmail
    val email: String,
    @field:Size(max = 50, message = "First name must be less than 50 characters")
    val firstName: String?,
    @field:Size(max = 50, message = "Last name must be less than 50 characters")
    val lastName: String?,
)
