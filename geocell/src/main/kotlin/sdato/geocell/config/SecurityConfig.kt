package sdato.geocell.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import sdato.geocell.http.Uris
import sdato.geocell.repository.UserSessionRepository
import sdato.geocell.service.CustomLogoutHandler

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userSessionRepository: UserSessionRepository,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // .csrf().disable()
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers(
                        Uris.Users.TOKEN,
                        Uris.Users.CREATE,
                        Uris.HOME,
                        Uris.About.ABOUT,
                        Uris.SystemInfo.INFO,
                    ).permitAll()
                    // .requestMatchers(ApiRoutes.LOGIN, ApiRoutes.REGISTER).permitAll()
                    .anyRequest().authenticated()
            }
            .logout { logout ->
                // logout.logoutUrl(ApiRoutes.LOGOUT)
                logout.logoutUrl(Uris.Users.LOGOUT)
                logout.deleteCookies("SESSION_TOKEN")
                logout.invalidateHttpSession(true)
                logout.addLogoutHandler(CustomLogoutHandler(userSessionRepository))
                logout.logoutSuccessHandler { _, response, _ ->
                    response.status = HttpStatus.OK.value()
                }
            }
            .httpBasic { }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(http: HttpSecurity): AuthenticationManager {
        return http.getSharedObject(AuthenticationManagerBuilder::class.java)
            .build()
    }
}
