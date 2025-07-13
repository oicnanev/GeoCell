package sdato.geocell.repository

import kotlinx.datetime.Instant
import sdato.geocell.domain.entities.PasswordValidationInfo
import sdato.geocell.domain.entities.Token
import sdato.geocell.domain.entities.TokenValidationInfo
import sdato.geocell.domain.entities.User

interface UserRepository {
    fun storeUser(
        username: String,
        passwordValidation: PasswordValidationInfo,
        // TODO: Add more fields as needed
    ): Int

    fun getUserById(id: Int): User?

    fun getUserUsernameById(id: Int): String?

    fun getUserByUsername(username: String): User?

    fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>?

    fun isUserStoredByUsername(username: String): Boolean

    fun createToken(token: Token, maxTokens: Int)

    fun updateTokenLastUsed(token: Token, now: Instant)

    fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int

    fun getUserIDByUsername(username1: String): Int
}
