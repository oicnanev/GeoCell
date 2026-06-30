package org.sdato.geocell.util

import org.sdato.geocell.domain.auth.AuthUserPrincipal
import org.sdato.geocell.dto.response.AuthUserResponse

fun AuthUserPrincipal.toResponse() = AuthUserResponse(
	id = userId,
	username = username,
	name = fullName,
	email = email,
	roles = authorities.mapNotNull { it.authority }.toSet()
)
