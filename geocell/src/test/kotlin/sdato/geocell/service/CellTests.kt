package sdato.geocell.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import sdato.geocell.model.Band
import sdato.geocell.model.Cell
import sdato.geocell.model.EnbGnb
import sdato.geocell.model.Location
import sdato.geocell.model.MccMnc
import sdato.geocell.model.User
import sdato.geocell.repository.BandRepository
import sdato.geocell.repository.CellRepository
import sdato.geocell.repository.EnbGnbRepository
import sdato.geocell.repository.LocationRepository
import sdato.geocell.repository.MccMncRepository
import sdato.geocell.repository.UserRepository
import java.time.LocalDate

@SpringBootTest
class CellTests {
    @Autowired
    private lateinit var cellRepository: CellRepository

    @Autowired
    private lateinit var bandRepository: BandRepository

    @Autowired
    private lateinit var enbGnbRepository: EnbGnbRepository

    @Autowired
    private lateinit var locationRepository: LocationRepository

    @Autowired
    private lateinit var mccMncRepository: MccMncRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        // Limpa as tabelas antes de cada teste
        cellRepository.deleteAll()
        bandRepository.deleteAll()
        enbGnbRepository.deleteAll()
        locationRepository.deleteAll()
        mccMncRepository.deleteAll()
        userRepository.deleteAll()

        // Cria dados de teste
        val band = bandRepository.save(Band(band = "Band 1", bandwidth = 20.0))
        val location = locationRepository.save(Location(zip4 = 1234, zip3 = 567))
        val enbGnb = enbGnbRepository.save(EnbGnb(enbGnb = 123, location = location))
        val mccMnc = mccMncRepository.save(MccMnc(mcc = 123, mnc = 456))
        val user =
            userRepository.save(
                User(
                    username = "test_user",
                    password = "password",
                    firstName = "Test",
                    lastName = "User",
                    email = "test@example.com",
                    isActive = true,
                ),
            )
    }

    @Test
    fun `test create and retrieve cell`() {
        // Busca os dados criados no setUp
        val band = bandRepository.findAll().first()
        val enbGnb = enbGnbRepository.findAll().first()
        val location = locationRepository.findAll().first()
        val mccMnc = mccMncRepository.findAll().first()
        val user = userRepository.findAll().first()

        // Cria uma nova Cell
        val cell =
            Cell(
                lacTac = "12345",
                ci = "67890",
                eciNci = "112233",
                cgi = "445566",
                paragonCgi = "778899",
                technology = 1,
                direction = 2,
                name = "Cell 1",
                created = LocalDate.now(),
                modified = LocalDate.now(),
                band = band,
                enbGnb = enbGnb,
                location = location,
                mccMnc = mccMnc,
                modifier = user,
                owner = user,
            )
        cellRepository.save(cell)

        // Busca a Cell
        val retrievedCell = cellRepository.findById(cell.id).orElse(null)
        assertNotNull(retrievedCell)
        assertEquals("Cell 1", retrievedCell?.name)
    }
}
