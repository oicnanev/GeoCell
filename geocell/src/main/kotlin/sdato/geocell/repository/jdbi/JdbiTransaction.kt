package sdato.geocell.repository.jdbi

import org.jdbi.v3.core.Handle
import sdato.geocell.repository.SystemInfoRepository
import sdato.geocell.repository.Transaction
import sdato.geocell.repository.UserRepository

class JdbiTransaction(
    private val handle: Handle
) : Transaction {
    override val userRepository: UserRepository = JdbiUserRepository(handle)
    override val systemInfoRepository: SystemInfoRepository = JdbiSystemInfoRepository(handle)

    override fun rollback() {
        handle.rollback()
    }
}
