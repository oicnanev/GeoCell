package sdato.geocell.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT)
class UsernameAlreadyExistsException(message: String) : RuntimeException(message)
