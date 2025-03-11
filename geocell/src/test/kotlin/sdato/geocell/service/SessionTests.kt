package sdato.geocell.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import sdato.geocell.model.Session
import sdato.geocell.repository.SessionRepository
import java.time.Instant

@SpringBootTest
class SessionTests {
    @Autowired
    private lateinit var sessionRepository: SessionRepository

    @BeforeEach
    fun setUp() {
        // Limpa a tabela de sessões antes de cada teste
        sessionRepository.deleteAll()
    }

    @Test
    fun `test create and retrieve session`() {
        // Cria uma nova sessão
        val session =
            Session(
                sessionKey = "test_session_key",
                sessionData = "test_session_data",
                expireDate = Instant.now(),
            )
        sessionRepository.save(session)

        // Busca a sessão
        val retrievedSession = sessionRepository.findBySessionKey("test_session_key")
        assertNotNull(retrievedSession)
        assertEquals("test_session_key", retrievedSession?.sessionKey)
    }
}
