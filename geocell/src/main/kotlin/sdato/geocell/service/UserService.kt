package sdato.geocell.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sdato.geocell.dto.request.CreateUserRequest
import sdato.geocell.dto.response.UserResponse
import sdato.geocell.exception.EmailAlreadyExistsException
import sdato.geocell.exception.UsernameAlreadyExistsException
import sdato.geocell.model.User
import sdato.geocell.repository.UserRepository
import java.time.Instant

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userSessionService: UserSessionService,
) {
    @Transactional
    fun createUser(request: CreateUserRequest): UserResponse {
        // Check if user already exists
        if (userRepository.existsByUsername(request.username)) {
            throw UsernameAlreadyExistsException("Username ${request.username} already exists")
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.email)) {
            throw EmailAlreadyExistsException("Email ${request.email} already exists")
        }

        // Create new user
        val user =
            User(
                username = request.username,
                password = passwordEncoder.encode(request.password),
                email = request.email,
                firstName = request.firstName ?: "",
                lastName = request.lastName ?: "",
                isActive = true,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            )

        val savedUser = userRepository.save(user)

        return userResponse(savedUser)
    }

    @Transactional(readOnly = true)
    fun getUserById(id: Long): UserResponse {
        val user =
            userRepository.findById(id)
                .orElseThrow { NoSuchElementException("User not found with id: $id") }

        return userResponse(user)
    }

    @Transactional
    fun deactivateUser(id: Long) {
        val user =
            userRepository.findById(id)
                .orElseThrow { NoSuchElementException("User not found with id: $id") }

        user.isActive = false
        user.updatedAt = Instant.now()
        userRepository.save(user)

        // Invalidates all user sessions
        userSessionService.invalidateAllSessionsForUser(id)
    }

    fun searchUsers(filter: UserSearchFilter): List<UserResponse> {
        val users =
            userRepository.searchUsersWithFilters(
                username = filter.username,
                email = filter.email,
                isActive = filter.isActive,
                groupIds = filter.groupIds ?: emptyList(),
            )

        return users.map { user ->
            userResponse(user = user)
        }
    }

    private fun userResponse(user: User) =
        UserResponse(
            id = user.id,
            username = user.username,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            isActive = user.isActive,
            groupIds = user.groups.map { it.id },
            roleIds = user.roles.map { it.id },
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
            lastLogin = user.lastLogin,
        )

    data class UserSearchFilter(
        val username: String?,
        val email: String?,
        val isActive: Boolean?,
        val groupIds: List<Long>?,
    )
}
