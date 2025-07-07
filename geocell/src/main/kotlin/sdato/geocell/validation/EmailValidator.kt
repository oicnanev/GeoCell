package sdato.geocell.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.util.regex.Pattern

class EmailValidator : ConstraintValidator<ValidEmail, String> {
    private val pattern =
        Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        )

    override fun isValid(
        email: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (email.isNullOrEmpty()) return false
        return pattern.matcher(email).matches()
    }
}
