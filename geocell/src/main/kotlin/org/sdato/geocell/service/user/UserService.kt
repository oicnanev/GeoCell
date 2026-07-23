package org.sdato.geocell.service.user

import org.sdato.geocell.domain.auth.AuthUserPrincipal
import org.sdato.geocell.domain.user.CreateUserRecord
import org.sdato.geocell.domain.user.UpdateUserRecord
import org.sdato.geocell.dto.request.CreateUserRequest
import org.sdato.geocell.dto.request.UpdateUserRequest
import org.sdato.geocell.dto.response.CreateUserResponse
import org.sdato.geocell.exception.ForbiddenOperationException
import org.sdato.geocell.exception.ResourceConflictException
import org.sdato.geocell.exception.ResourceNotFoundException
import org.sdato.geocell.repository.department.DepartmentRepository
import org.sdato.geocell.repository.user.UserRepository
import org.sdato.geocell.validator.CreateUserRequestValidator
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Profile("!test")
class UserService(
	private val userRepository: UserRepository,
	private val departmentRepository: DepartmentRepository,
	private val passwordEncoder: PasswordEncoder,
	private val createUserRequestValidator: CreateUserRequestValidator
) {
	@Transactional
	fun createUser(request: CreateUserRequest, principal: AuthUserPrincipal): CreateUserResponse {
		assertSuperuser(principal)
		createUserRequestValidator.validate(request)

		val normalizedUsername = request.username.trim()
		val normalizedName = request.name.trim()
		val normalizedEmail = request.email.trim().lowercase()

		if (!departmentRepository.existsById(request.departmentId)) {
			throw ResourceNotFoundException("Department with id ${request.departmentId} was not found")
		}
		if (userRepository.existsByUsername(normalizedUsername)) {
			throw ResourceConflictException("Username '$normalizedUsername' is already in use")
		}
		if (userRepository.existsByEmail(normalizedEmail)) {
			throw ResourceConflictException("Email '$normalizedEmail' is already in use")
		}
		val passwordHash = passwordEncoder.encode(request.password)
			?: throw IllegalStateException("Password encoder returned null hash")

		val userId = userRepository.createUser(
			CreateUserRecord(
				username = normalizedUsername,
				passwordHash = passwordHash,
				name = normalizedName,
				email = normalizedEmail,
				isSuperuser = request.isSuperuser,
				isAnalyst = request.isAnalyst,
				isOperationAdmin = request.isOperationAdmin,
				mapType = request.mapType,
				showGrid = request.showGrid,
				showCounties = request.showCounties
			)
		)
		userRepository.assignDepartment(userId, request.departmentId)

		return CreateUserResponse(
			id = userId,
			username = normalizedUsername,
			name = normalizedName,
			email = normalizedEmail,
			departmentId = request.departmentId,
			isSuperuser = request.isSuperuser,
			isAnalyst = request.isAnalyst,
			isOperationAdmin = request.isOperationAdmin,
			mapType = request.mapType,
			showGrid = request.showGrid,
			showCounties = request.showCounties
		)
	}

	fun listUsers(principal: AuthUserPrincipal): List<CreateUserResponse> {
		assertSuperuser(principal)
		return userRepository.findAll().map(::toResponse)
	}

	fun getUserById(userId: Long, principal: AuthUserPrincipal): CreateUserResponse {
		assertSuperuser(principal)
		val user = userRepository.findById(userId)
			?: throw ResourceNotFoundException("User with id $userId was not found")

		return toResponse(user)
	}

	@Transactional
	fun updateUser(userId: Long, request: UpdateUserRequest, principal: AuthUserPrincipal): CreateUserResponse {
		assertSuperuser(principal)
		createUserRequestValidator.validate(request)
		userRepository.findById(userId)
			?: throw ResourceNotFoundException("User with id $userId was not found")

		val normalizedUsername = request.username.trim()
		val normalizedName = request.name.trim()
		val normalizedEmail = request.email.trim().lowercase()

		if (!departmentRepository.existsById(request.departmentId)) {
			throw ResourceNotFoundException("Department with id ${request.departmentId} was not found")
		}
		if (userRepository.existsByUsernameExcludingId(normalizedUsername, userId)) {
			throw ResourceConflictException("Username '$normalizedUsername' is already in use")
		}
		if (userRepository.existsByEmailExcludingId(normalizedEmail, userId)) {
			throw ResourceConflictException("Email '$normalizedEmail' is already in use")
		}
		val passwordHash = request.password?.let {
			passwordEncoder.encode(it) ?: throw IllegalStateException("Password encoder returned null hash")
		}

		userRepository.updateUser(
			userId,
			UpdateUserRecord(
				username = normalizedUsername,
				passwordHash = passwordHash,
				name = normalizedName,
				email = normalizedEmail,
				isSuperuser = request.isSuperuser,
				isAnalyst = request.isAnalyst,
				isOperationAdmin = request.isOperationAdmin,
				mapType = request.mapType,
				showGrid = request.showGrid,
				showCounties = request.showCounties
			)
		)
		userRepository.replaceDepartment(userId, request.departmentId)

		return CreateUserResponse(
			id = userId,
			username = normalizedUsername,
			name = normalizedName,
			email = normalizedEmail,
			departmentId = request.departmentId,
			isSuperuser = request.isSuperuser,
			isAnalyst = request.isAnalyst,
			isOperationAdmin = request.isOperationAdmin,
			mapType = request.mapType,
			showGrid = request.showGrid,
			showCounties = request.showCounties
		)
	}

	@Transactional
	fun deleteUser(userId: Long, principal: AuthUserPrincipal) {
		assertSuperuser(principal)
		userRepository.findById(userId)
			?: throw ResourceNotFoundException("User with id $userId was not found")
		userRepository.deleteUser(userId)
	}

	private fun assertSuperuser(principal: AuthUserPrincipal) {
		if (!principal.isSuperuser) {
			throw ForbiddenOperationException("Only superusers can manage users")
		}
	}

	private fun toResponse(user: org.sdato.geocell.domain.user.UserRecord) =
		CreateUserResponse(
			id = user.id,
			username = user.username,
			name = user.name,
			email = user.email,
			departmentId = user.departmentId,
			isSuperuser = user.isSuperuser,
			isAnalyst = user.isAnalyst,
			isOperationAdmin = user.isOperationAdmin,
			mapType = user.mapType,
			showGrid = user.showGrid,
			showCounties = user.showCounties
		)
}
