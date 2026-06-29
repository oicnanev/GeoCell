package org.sdato.geocell.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AuthUserPrincipal(
	val userId: Long,
	private val usernameValue: String,
	val fullName: String,
	val email: String,
	val isSuperuser: Boolean,
	val isAnalyst: Boolean,
	val isOperationAdmin: Boolean,
	private val active: Boolean
) : UserDetails {
	override fun getAuthorities(): Collection<GrantedAuthority> {
		val authorities = mutableListOf<GrantedAuthority>(SimpleGrantedAuthority("ROLE_USER"))
		if (isSuperuser) authorities.add(SimpleGrantedAuthority("ROLE_SUPERUSER"))
		if (isAnalyst) authorities.add(SimpleGrantedAuthority("ROLE_ANALYST"))
		if (isOperationAdmin) authorities.add(SimpleGrantedAuthority("ROLE_OPERATION_ADMIN"))
		return authorities
	}

	override fun getPassword(): String = ""

	override fun getUsername(): String = usernameValue

	override fun isAccountNonExpired(): Boolean = true

	override fun isAccountNonLocked(): Boolean = true

	override fun isCredentialsNonExpired(): Boolean = true

	override fun isEnabled(): Boolean = active
}
