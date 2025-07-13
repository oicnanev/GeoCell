package sdato.geocell.repository

interface Transaction {
    // TODO: Add more repositories as needed
    val userRepository: UserRepository
    val systemInfoRepository: SystemInfoRepository

    fun rollback()
}