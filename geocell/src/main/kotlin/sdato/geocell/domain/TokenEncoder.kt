package sdato.geocell.domain

import sdato.geocell.domain.entities.TokenValidationInfo

interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
}
