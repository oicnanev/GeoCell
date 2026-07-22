package org.sdato.geocell.controller.cell

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.sdato.geocell.testsupport.H2GeoCellTestDatabase
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class CellControllerTest {
	private lateinit var mockMvc: MockMvc

	@BeforeEach
	fun setUp() {
		val env = H2GeoCellTestDatabase.create()
		mockMvc = MockMvcBuilders
			.standaloneSetup(env.controller)
			.setMessageConverters(MappingJackson2HttpMessageConverter(ObjectMapper().findAndRegisterModules()))
			.build()
	}

	@Test
	fun `search by enb gnb returns cells`() {
		mockMvc.perform(get("/api/cells/search/enb-gnb").param("enbGnb", "1001"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("\$[0].paragonCgi").value("268-3-10001"))
			.andExpect(jsonPath("\$[1].cgi").value("268-3-10000"))
	}

	@Test
	fun `search by county returns caop polygon at top level`() {
		mockMvc.perform(
			get("/api/cells/search/county")
				.param("districtId", "11")
				.param("countyId", "1")
				.param("mnc", "3")
		)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.caopPolygonGeoJson").exists())
			.andExpect(jsonPath("$.cells.length()").value(2))
	}

	@Test
	fun `search circle applies band filter`() {
		mockMvc.perform(
			get("/api/cells/search/circle")
				.param("lat", "38.72")
				.param("lon", "-9.15")
				.param("radiusKm", "2")
				.param("mnc", "3")
				.param("band", "800")
		)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.cells.length()").value(1))
	}

	@Test
	fun `search lac tac returns all matching cells`() {
		mockMvc.perform(
			get("/api/cells/search/lac-tac")
				.param("mcc", "268")
				.param("mnc", "3")
				.param("lacTac", "1234")
		)
			.andExpect(status().isOk())
			.andExpect(jsonPath("\$[0].cgi").value("268-3-30000"))
			.andExpect(jsonPath("\$[1].paragonCgi").value("268-3-10001"))
			.andExpect(jsonPath("\$[2].cgi").value("268-3-10000"))
	}
}
