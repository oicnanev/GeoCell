package sdato.geocell.service

/*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import sdato.geocell.model.User
import sdato.geocell.repository.UserRepository

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTests {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var userDetailsService: CustomUserDetailsService

    @BeforeEach
    fun setUp() {
        // Limpa a tabela de users antes de cada teste
        // userRepository.deleteAll()

        // Cria o utilizador oicnanev para testes
        val user =
            User(
                username = "oicnanev2",
                password = passwordEncoder.encode("Yd4xee35$"),
                firstName = "First",
                lastName = "Last",
                email = "oicnanev@example.com",
                isActive = true,
            )
        userRepository.save(user)
    }

    @Test
    fun `test authentication of existing user`() {
        // Tenta carregar o utilizador oicnanev
        val userDetails: UserDetails = userDetailsService.loadUserByUsername("oicnanev2")

        // Verifica se o username está correto
        assertEquals("oicnanev2", userDetails.username)

        // Verifica se a senha está correta
        assertTrue(passwordEncoder.matches("Yd4xee35$", userDetails.password))
    }

    @Test
    fun `test creation and authentication of new user`() {
        // Cria um novo utilizador springTest
        val newUser =
            User(
                username = "springTest",
                password = passwordEncoder.encode("Spring123"),
                firstName = "Spring",
                lastName = "Test",
                email = "springtest@example.com",
                isActive = true,
            )
        userRepository.save(newUser)

        // Tenta carregar o novo utilizador
        val userDetails: UserDetails = userDetailsService.loadUserByUsername("springTest")

        // Verifica se o username está correto
        assertEquals("springTest", userDetails.username)

        // Verifica se a senha está correta
        assertTrue(passwordEncoder.matches("Spring123", userDetails.password))
    }
}
*/
