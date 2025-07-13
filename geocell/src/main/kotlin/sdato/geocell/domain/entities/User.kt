package sdato.geocell.domain.entities

data class User(
    val id: Int,
    val username: String,
    val passwordValidation: PasswordValidationInfo,
)
