package org.sdato.geocell.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException
import java.time.OffsetDateTime

@RestControllerAdvice
class ApiExceptionHandler {

	@ExceptionHandler(MaxUploadSizeExceededException::class)
	fun handleMaxUploadSizeExceeded(
		ex: MaxUploadSizeExceededException,
		request: HttpServletRequest
	): ResponseEntity<Map<String, Any>> {
		val status = HttpStatus.PAYLOAD_TOO_LARGE
		val body = mapOf(
			"timestamp" to OffsetDateTime.now().toString(),
			"status" to status.value(),
			"error" to status.reasonPhrase,
			"message" to "File exceeds maximum allowed upload size (30MB)",
			"path" to request.requestURI
		)
		return ResponseEntity.status(status).body(body)
	}
}
