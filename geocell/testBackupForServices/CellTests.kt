package sdato.geocell.service

/*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import sdato.geocell.repository.BandRepository
import sdato.geocell.repository.CellRepository
import sdato.geocell.repository.EnbGnbRepository
import sdato.geocell.repository.LocationRepository
import sdato.geocell.repository.MccMncRepository
import sdato.geocell.repository.UserRepository

@SpringBootTest
@ActiveProfiles("test")
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

   /* @BeforeEach
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
        val band = Band(
            id = 1,
            band = 700,
            bandwidth = null,
            uplinkFreq = null,
            downlinkFreq = null,
            earfcn = null
        )  // bandRepository.findAll().first()

        val location = Location(
            id = 1,
            coordinates = Point(
                Coordinate(x = 0, y = 0, z = 0),
                PrecisionModel(0.3),

            )
            address = "",
            address1 = "",
            zip4 = 1000,
            zip3 = 0,
            postalDesignation = "",
            county = County(
                id = 1,
                idCounty = "01",
                county = "Lisboa",
                polygon = null,
                district =  District(
                    id = "01",
                    district = "Lisboa",
                    polygon = null,
                    country = Country(
                        name = "Portugal",
                        code = "PT",
                        polygon = null,
                        flag = ""
                    )
                ),
            ),
        ) // locationRepository.findAll().first()

        val enbGnb = EnbGnb(
            id = 1,
            enbGnb = 1,
            location = location
        ) // enbGnbRepository.findAll().first()

        val mccMnc = 1 // mccMncRepository.findAll().first()
        val user = 1 // userRepository.findAll().first()

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
    } */
}
*/
