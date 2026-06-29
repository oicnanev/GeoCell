package org.sdato.geocell.security

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@ConditionalOnBean(SessionCookieAuthenticationFilter::class)
@Profile("!test")
class SecurityConfig {
	@Bean
	fun securityFilterChain(
		http: HttpSecurity,
		sessionCookieAuthenticationFilter: SessionCookieAuthenticationFilter
	): SecurityFilterChain =
		http
			.csrf(AbstractHttpConfigurer<*, *>::disable)
			.httpBasic(AbstractHttpConfigurer<*, *>::disable)
			.formLogin(AbstractHttpConfigurer<*, *>::disable)
			.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
			.exceptionHandling { it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) }
			.authorizeHttpRequests {
				it.requestMatchers("/api/auth/login").permitAll()
				it.anyRequest().authenticated()
			}
			.addFilterBefore(sessionCookieAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
			.build()
}
