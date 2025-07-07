package sdato.geocell.service

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sdato.geocell.repository.UserRepository

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        val user =
            userRepository.findByUsername(username)
                ?: throw UsernameNotFoundException("User not found with username: $username")

        val authorities = mutableSetOf<GrantedAuthority>()

        // Add user roles
        user.roles.forEach { role ->
            authorities.add(SimpleGrantedAuthority("ROLE_${role.name}"))
        }

        return org.springframework.security.core.userdetails.User(
            user.username,
            user.password,
            user.isActive,
            true,
            true,
            true,
            authorities.toList(),
        )
    }
}
