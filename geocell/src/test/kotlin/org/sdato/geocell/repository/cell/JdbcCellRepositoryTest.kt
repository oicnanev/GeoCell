package org.sdato.geocell.repository.cell

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.sdato.geocell.testsupport.H2GeoCellTestDatabase
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JdbcCellRepositoryTest {
	private lateinit var repository: CellRepository

	@BeforeEach
	fun setUp() {
		repository = H2GeoCellTestDatabase.create().repository
	}

	@Test
	fun `find by CGI returns exact match`() {
		val cells = repository.findByCgi("268-3-10000")
		assertEquals(1, cells.size)
		assertEquals("268-3-10000", cells.first().cgi)
	}

	@Test
	fun `find districts by country returns ordered districts`() {
		val districts = repository.findDistrictsByCountry("Portugal")
		assertEquals(listOf("01", "11"), districts.map { it.id })
	}

	@Test
	fun `find counties by district returns county list`() {
		val counties = repository.findCountiesByDistrict("11")
		assertEquals(listOf("Amadora", "Oeiras"), counties.map { it.county })
	}

	@Test
	fun `find cells by administrative area filters county and technology`() {
		val cells = repository.findCellsByAdministrativeArea(
			districtId = "11",
			countyId = 1L,
			mnc = 3,
			technologies = setOf(4, 5)
		)

		assertEquals(listOf(2L, 1L), cells.map { it.id })
		assertTrue(cells.all { it.caopPolygonGeoJson?.contains("\"type\":\"Polygon\"") == true })
	}

	@Test
	fun `find cells by lac tac returns all matching cells`() {
		val cells = repository.findCellsByLacTac(268, 3, "1234")
		assertEquals(listOf(4L, 2L, 1L), cells.map { it.id })
	}

	@Test
	fun `find cells by enb gnb returns all matching cells`() {
		val cells = repository.findCellsByEnbGnb(1001)
		assertEquals(listOf(2L, 1L), cells.map { it.id })
	}

	@Test
	fun `find nearby cells filters by band`() {
		val cells = repository.findNearbyCells(
			latitude = 38.72,
			longitude = -9.15,
			radiusMeters = 2_000.0,
			mnc = 3,
			band = "800",
			technologies = setOf(4)
		)

		assertEquals(listOf(1L), cells.map { it.id })
	}

	@Test
	fun `find cells in circle returns cells within radius`() {
		val cells = repository.findCellsInCircle(
			latitude = 38.72,
			longitude = -9.15,
			radiusMeters = 2_000.0,
			mnc = 3,
			band = null,
			technologies = null
		)

		assertEquals(listOf(1L, 2L), cells.map { it.id })
	}

	@Test
	fun `find cells in bbox returns cells inside envelope`() {
		val cells = repository.findCellsInBbox(
			lat1 = 38.71,
			lon1 = -9.16,
			lat2 = 38.74,
			lon2 = -9.13,
			mnc = 3,
			band = "700",
			technologies = null
		)

		assertEquals(listOf(2L), cells.map { it.id })
	}

	@Test
	fun `find lac tac coverage polygon returns geojson`() {
		val polygon = repository.findLacTacCoveragePolygon(268, 3, "1234")
		assertNotNull(polygon)
		assertTrue(polygon.contains("\"type\":\"Polygon\"") || polygon.contains("\"type\":\"Point\""))
	}
}
