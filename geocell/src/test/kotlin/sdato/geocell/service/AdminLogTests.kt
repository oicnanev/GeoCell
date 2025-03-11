package sdato.geocell.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import sdato.geocell.model.AdminLog
import sdato.geocell.model.ContentType
import sdato.geocell.model.User
import sdato.geocell.repository.AdminLogRepository
import sdato.geocell.repository.ContentTypeRepository
import sdato.geocell.repository.UserRepository
import java.time.Instant

@SpringBootTest
class AdminLogTests {

    @Autowired
    private lateinit var adminLogRepository: AdminLogRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var contentTypeRepository: ContentTypeRepository

    @BeforeEach
    fun setUp() {
        // Limpa as tabelas antes de cada teste
        adminLogRepository.deleteAll()
        userRepository.deleteAll()
        contentTypeRepository.deleteAll()

        // Cria um usuário
        val user = User(
            username = "admin_user",
            password = "admin_password",
            firstName = "Admin",
            lastName = "User",
            email = "admin@example.com",
            isActive = true
        )
        userRepository.save(user)

        // Cria um ContentType
        val contentType = ContentType(
            appLabel = "auth",
            model = "user"
        )
        contentTypeRepository.save(contentType)
    }

    @Test
    fun `test create and retrieve admin log`() {
        // Busca o usuário e o ContentType criados no setUp
        val user = userRepository.findByUsername("admin_user")!!
        val contentType = contentTypeRepository.findAll().first()

        // Cria um AdminLog
        val adminLog = AdminLog(
            actionTime = Instant.now(),
            objectId = "1",
            objectRepr = "User object",
            actionFlag = 1,
            changeMessage = "Created user",
            contentType = contentType,
            user = user
        )
        adminLogRepository.save(adminLog)

        // Busca o AdminLog
        val retrievedAdminLog = adminLogRepository.findById(adminLog.id).orElse(null)
        assertNotNull(retrievedAdminLog)
        assertEquals("User object", retrievedAdminLog?.objectRepr)
    }
}
