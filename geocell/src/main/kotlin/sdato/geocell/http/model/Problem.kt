package sdato.geocell.http.model

import org.springframework.http.ResponseEntity
import java.net.URI

class Problem(
    typeUri: URI
) {
    val type = typeUri.toASCIIString()

    companion object {
        const val MEDIA_TYPE = "application/problem+json"
        const val GIT_URI = "https://github.com/oicnanev/GeoCell/"
        const val DOCS_URI = "docs/problems/"

        fun response(status: Int, problem: Problem) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body<Any>(problem)

        val userAlreadyExists = Problem(URI("${GIT_URI}${DOCS_URI}user-already-exists"))
        val insecurePassword = Problem(URI("${GIT_URI}${DOCS_URI}insecure-password"))
        val userOrPasswordAreInvalid = Problem(URI("${GIT_URI}${DOCS_URI}user-or-password-are-invalid"))
        val userNotFound = Problem(URI("${GIT_URI}${DOCS_URI}user-not-found"))
        val invalidRequestContent = Problem(URI("${GIT_URI}${DOCS_URI}invalid-request-content"))
        val invalidToken = Problem(URI("${GIT_URI}${DOCS_URI}invalid-token"))
    }
}