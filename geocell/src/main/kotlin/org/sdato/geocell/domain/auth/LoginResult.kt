package org.sdato.geocell.domain.auth

data class LoginResult(
	val cookieHeader: String,
	val principal: AuthUserPrincipal
)
