package org.sdato.geocell.service.cell

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.sdato.geocell.domain.auth.AuthUserPrincipal
import org.sdato.geocell.dto.request.CellBandRequest
import org.sdato.geocell.dto.request.CellEnbGnbRequest
import org.sdato.geocell.dto.request.CellLocationRequest
import org.sdato.geocell.dto.request.CellMccMncRequest
import org.sdato.geocell.dto.request.CellUpsertRequest
import org.sdato.geocell.testsupport.H2GeoCellTestDatabase
import org.springframework.mock.web.MockMultipartFile
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CellServiceTest {
	private lateinit var env: H2GeoCellTestDatabase.Environment

	private val principal = AuthUserPrincipal(
		userId = 1,
		usernameValue = "admin",
		fullName = "Admin User",
		email = "admin@example.com",
		isSuperuser = true,
		isAnalyst = true,
		isOperationAdmin = true,
		active = true
	)

	@BeforeEach
	fun setUp() {
		env = H2GeoCellTestDatabase.create()
	}

	@Test
	fun `county search returns caop polygon once at response level`() {
		val response = env.service.getCellsByAdministrativeArea(
			districtId = "11",
			countyId = 1L,
			mnc = 3,
			techGenerations = listOf("4G", "5G")
		)

		assertEquals("11", response.districtId)
		assertEquals(1L, response.countyId)
		assertNotNull(response.caopPolygonGeoJson)
		assertTrue(response.cells.isNotEmpty())
		assertTrue(response.cells.all { it.caopPolygonGeoJson == null })
	}

	@Test
	fun `lac tac search returns matching cells`() {
		val cells = env.service.getCellsByLacTac(268, 3, "1234")
		assertEquals(
			listOf("268-3-30000", "268-3-10001", "268-3-10000"),
			cells.map { it.cgi ?: it.paragonCgi }
		)
	}

	@Test
	fun `lac tac coverage polygon returns geojson`() {
		val response = env.service.getLacTacCoveragePolygon(268, 3, "1234")
		assertEquals(268, response.mcc)
		assertEquals(3, response.mnc)
		assertEquals("1234", response.lacTac)
		assertNotNull(response.polygonGeoJson)
	}

	@Test
	fun `enb gnb search returns matching cells`() {
		val cells = env.service.getCellsByEnbGnb(1001)
		assertEquals(listOf("268-3-10001", "268-3-10000"), cells.map { it.cgi ?: it.paragonCgi })
	}

	@Test
	fun `circle and bbox searches accept band filter`() {
		val circle = env.service.getCellsInCircle(
			latitude = 38.72,
			longitude = -9.15,
			radiusKm = 2.0,
			mnc = 3,
			band = "800",
			techGenerations = null
		)
		assertEquals(listOf("268-3-10000"), circle.cells.map { it.cgi ?: it.paragonCgi })

		val bbox = env.service.getCellsInBbox(
			lat1 = 38.71,
			lon1 = -9.16,
			lat2 = 38.74,
			lon2 = -9.13,
			mnc = 3,
			band = "700",
			techGenerations = null
		)
		assertEquals(listOf("268-3-10001"), bbox.cells.map { it.cgi ?: it.paragonCgi })
	}

	@Test
	fun `create update and delete cell work end to end`() {
		val createRequest = CellUpsertRequest(
			cgi = "269-10-11111",
			technology = 4,
			azimuth = 60,
			name = "Created cell",
			location = CellLocationRequest(
				latitude = 38.75,
				longitude = -9.17,
				zip4 = 2000,
				zip3 = 200,
				countyId = 1L
			),
			enbGnb = CellEnbGnbRequest(enbGnb = 2001),
			mccMnc = CellMccMncRequest(mcc = 269, mnc = 10),
			band = CellBandRequest(band = "900")
		)

		val created = env.service.createCell(createRequest, principal)
		assertEquals("269-10-11111", created.cgi)
		assertEquals("900", created.band?.band)
		assertNotNull(created.polygonGeoJson)

		val updateRequest = createRequest.copy(
			azimuth = 180,
			name = "Updated cell"
		)
		val updated = env.service.updateCell(created.id, updateRequest, principal)
		assertEquals(180, updated.azimuth)
		assertEquals("Updated cell", updated.name)
		assertNotNull(updated.polygonGeoJson)

		env.service.deleteCell(created.id)
		assertNull(env.repository.findById(created.id))
	}

	@Test
	fun `import csv inserts new cells`() {
		val csv = """
			CGI_ECGI_NCGI;MCC;MNC;TECNOLOGIA;AZIMUTE;LATITUDE;LONGITUDE;BANDA;NOME;MORADA;MORADA1;CP4;CP3;DESIGNACAO_POSTAL;DATA;ENB_GNB;CONCELHO_CD
			269-11-22222;269;11;4;45;38.76;-9.18;1800;Imported cell;Rua X;;2001;201;Lisboa;2026-07-22;3001;1101
		""".trimIndent()
		val file = MockMultipartFile("file", "cells.csv", "text/csv", csv.toByteArray())

		val result = env.service.importCellsCsv(file, principal)

		assertEquals(1, result.rowsProcessed)
		assertEquals(1, result.inserted)
		assertEquals(1, result.polygonsUpserted)
		assertNotNull(env.repository.findByCgi("269-11-22222").firstOrNull())
	}
}
