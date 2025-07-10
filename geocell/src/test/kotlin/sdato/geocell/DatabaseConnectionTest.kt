package sdato.geocell

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.sql.DataSource

@SpringBootTest
class DatabaseConnectionTest(
    @Autowired private val dataSource: DataSource,
) {
    @Test
    fun testConnection() {
        assertDoesNotThrow {
            dataSource.connection.use { conn ->
                assertTrue(conn.isValid(1))
            }
        }
    }
}
