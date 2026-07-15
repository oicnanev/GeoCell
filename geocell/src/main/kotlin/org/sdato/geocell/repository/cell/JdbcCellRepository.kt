package org.sdato.geocell.repository.cell

import org.sdato.geocell.domain.cell.CellBandRecord
import org.sdato.geocell.domain.cell.CellBandWriteRecord
import org.sdato.geocell.domain.cell.CellDetailsRecord
import org.sdato.geocell.domain.cell.CellEnbGnbRecord
import org.sdato.geocell.domain.cell.CellLocationRecord
import org.sdato.geocell.domain.cell.CellLocationWriteRecord
import org.sdato.geocell.domain.cell.CellMccMncRecord
import org.sdato.geocell.domain.cell.CellMccMncWriteRecord
import org.sdato.geocell.domain.cell.CellWriteRecord
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.OffsetDateTime

@Repository
@Profile("!test")
class JdbcCellRepository(
	private val jdbcTemplate: JdbcTemplate
) : CellRepository {

	override fun findByCgi(cgi: String): List<CellDetailsRecord> =
		jdbcTemplate.query(
			"$baseSelect WHERE c.cgi = ? OR c.paragon_cgi = ? ORDER BY c.id DESC",
			{ rs, _ -> rs.toCellDetailsRecord() },
			cgi,
			cgi
		)

	override fun findByIdentifiers(cgi: String?, paragonCgi: String?): CellDetailsRecord? {
		if (cgi == null && paragonCgi == null) {
			return null
		}

		val conditions = buildList {
			if (cgi != null) add("c.cgi = ?")
			if (paragonCgi != null) add("c.paragon_cgi = ?")
		}
		val params = buildList<Any> {
			if (cgi != null) add(cgi)
			if (paragonCgi != null) add(paragonCgi)
		}

		return jdbcTemplate.query(
			"""
			$baseSelect
			WHERE ${conditions.joinToString(" OR ")}
			ORDER BY c.id DESC
			""".trimIndent(),
			{ rs, _ -> rs.toCellDetailsRecord() },
			*params.toTypedArray()
		).firstOrNull()
	}

	override fun findNearbyCells(
		latitude: Double,
		longitude: Double,
		radiusMeters: Double,
		mnc: Int?,
		technologies: Set<Int>?
	): List<CellDetailsRecord> {
		val sql = StringBuilder(
			"""
			$baseSelect
			WHERE l.coordinates IS NOT NULL
			  AND ST_DWithin(
			    l.coordinates::geography,
			    ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography,
			    ?
			  )
			""".trimIndent()
		)
		val params = mutableListOf<Any>(longitude, latitude, radiusMeters)

		if (mnc != null) {
			sql.append("\n  AND m.mnc = ?")
			params.add(mnc)
		}
		if (!technologies.isNullOrEmpty()) {
			sql.append("\n  AND c.technology IN (${technologies.joinToString(",") { "?" }})")
			params.addAll(technologies)
		}

		sql.append(
			"""
			
			ORDER BY ST_Distance(
			  l.coordinates::geography,
			  ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography
			), c.id DESC
			""".trimIndent()
		)
		params.add(longitude)
		params.add(latitude)

		return jdbcTemplate.query(
			sql.toString(),
			{ rs, _ -> rs.toCellDetailsRecord() },
			*params.toTypedArray()
		)
	}

	override fun findById(id: Long): CellDetailsRecord? =
		jdbcTemplate.query(
			"$baseSelect WHERE c.id = ?",
			{ rs, _ -> rs.toCellDetailsRecord() },
			id
		).firstOrNull()

	override fun findCountyId(districtId: String, countyId: String): Long? =
		jdbcTemplate.query(
			"""
			SELECT id
			FROM county
			WHERE district_id = ? AND id_county = ?
			LIMIT 1
			""".trimIndent(),
			{ rs, _ -> rs.getLong("id") },
			districtId,
			countyId
		).firstOrNull()

	override fun findMccMncId(mcc: Int, mnc: Int): Long? =
		jdbcTemplate.query(
			"""
			SELECT id
			FROM mccmnc
			WHERE mcc = ? AND mnc = ?
			LIMIT 1
			""".trimIndent(),
			{ rs, _ -> rs.getLong("id") },
			mcc,
			mnc
		).firstOrNull()

	override fun findLocationId(record: CellLocationWriteRecord): Long? =
		jdbcTemplate.query(
			"""
			SELECT id
			FROM location
			WHERE ST_Equals(coordinates, ST_SetSRID(ST_MakePoint(?, ?), 4326))
			  AND address IS NOT DISTINCT FROM ?
			  AND address1 IS NOT DISTINCT FROM ?
			  AND zip4 = ?
			  AND zip3 = ?
			  AND postal_designation IS NOT DISTINCT FROM ?
			  AND id_county_id IS NOT DISTINCT FROM ?
			ORDER BY id
			LIMIT 1
			""".trimIndent(),
			{ rs, _ -> rs.getLong("id") },
			record.longitude,
			record.latitude,
			record.address,
			record.address1,
			record.zip4,
			record.zip3,
			record.postalDesignation,
			record.countyId
		).firstOrNull()

	override fun findBandId(record: CellBandWriteRecord): Long? =
		jdbcTemplate.query(
			"""
			SELECT id
			FROM band
			WHERE band IS NOT DISTINCT FROM ?
			  AND bandwidth IS NOT DISTINCT FROM ?
			  AND uplink_freq IS NOT DISTINCT FROM ?
			  AND downlink_freq IS NOT DISTINCT FROM ?
			  AND earfcn IS NOT DISTINCT FROM ?
			ORDER BY id
			LIMIT 1
			""".trimIndent(),
			{ rs, _ -> rs.getLong("id") },
			record.band,
			record.bandwidth,
			record.uplinkFreq,
			record.downlinkFreq,
			record.earfcn
		).firstOrNull()

	override fun findEnbGnbId(enbGnb: Int, locationId: Long): Long? =
		jdbcTemplate.query(
			"""
			SELECT id
			FROM enbgnb
			WHERE enb_gnb = ? AND location_id = ?
			ORDER BY id
			LIMIT 1
			""".trimIndent(),
			{ rs, _ -> rs.getLong("id") },
			enbGnb,
			locationId
		).firstOrNull()

	override fun insertLocation(record: CellLocationWriteRecord): Long =
		jdbcTemplate.queryForObject(
			"""
			INSERT INTO location (coordinates, address, address1, zip4, zip3, postal_designation, id_county_id)
			VALUES (ST_SetSRID(ST_MakePoint(?, ?), 4326), ?, ?, ?, ?, ?, ?)
			RETURNING id
			""".trimIndent(),
			Long::class.java,
			record.longitude,
			record.latitude,
			record.address,
			record.address1,
			record.zip4,
			record.zip3,
			record.postalDesignation,
			record.countyId
		) ?: throw IllegalStateException("Failed to create location")

	override fun updateLocation(id: Long, record: CellLocationWriteRecord) {
		jdbcTemplate.update(
			"""
			UPDATE location
			SET coordinates = ST_SetSRID(ST_MakePoint(?, ?), 4326),
			    address = ?,
			    address1 = ?,
			    zip4 = ?,
			    zip3 = ?,
			    postal_designation = ?,
			    id_county_id = ?
			WHERE id = ?
			""".trimIndent(),
			record.longitude,
			record.latitude,
			record.address,
			record.address1,
			record.zip4,
			record.zip3,
			record.postalDesignation,
			record.countyId,
			id
		)
	}

	override fun insertBand(record: CellBandWriteRecord): Long =
		jdbcTemplate.queryForObject(
			"""
			INSERT INTO band (band, bandwidth, uplink_freq, downlink_freq, earfcn)
			VALUES (?, ?, ?, ?, ?)
			RETURNING id
			""".trimIndent(),
			Long::class.java,
			record.band,
			record.bandwidth,
			record.uplinkFreq,
			record.downlinkFreq,
			record.earfcn
		) ?: throw IllegalStateException("Failed to create band")

	override fun updateBand(id: Long, record: CellBandWriteRecord) {
		jdbcTemplate.update(
			"""
			UPDATE band
			SET band = ?, bandwidth = ?, uplink_freq = ?, downlink_freq = ?, earfcn = ?
			WHERE id = ?
			""".trimIndent(),
			record.band,
			record.bandwidth,
			record.uplinkFreq,
			record.downlinkFreq,
			record.earfcn,
			id
		)
	}

	override fun upsertMccMnc(record: CellMccMncWriteRecord): Long =
		jdbcTemplate.queryForObject(
			"""
			INSERT INTO mccmnc (type, mcc, mnc, brand, "operator", status, bands, notes, country_id)
			VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
			ON CONFLICT (mcc, mnc)
			DO UPDATE SET
				type = EXCLUDED.type,
				brand = EXCLUDED.brand,
				"operator" = EXCLUDED."operator",
				status = EXCLUDED.status,
				bands = EXCLUDED.bands,
				notes = EXCLUDED.notes,
				country_id = EXCLUDED.country_id
			RETURNING id
			""".trimIndent(),
			Long::class.java,
			record.type,
			record.mcc,
			record.mnc,
			record.brand,
			record.operatorName,
			record.status,
			record.bands,
			record.notes,
			record.countryId
		) ?: throw IllegalStateException("Failed to upsert mccmnc")

	override fun insertEnbGnb(enbGnb: Int, locationId: Long): Long =
		jdbcTemplate.queryForObject(
			"""
			INSERT INTO enbgnb (enb_gnb, location_id)
			VALUES (?, ?)
			RETURNING id
			""".trimIndent(),
			Long::class.java,
			enbGnb,
			locationId
		) ?: throw IllegalStateException("Failed to create enbgnb")

	override fun updateEnbGnb(id: Long, enbGnb: Int, locationId: Long) {
		jdbcTemplate.update(
			"UPDATE enbgnb SET enb_gnb = ?, location_id = ? WHERE id = ?",
			enbGnb,
			locationId,
			id
		)
	}

	override fun insertCell(record: CellWriteRecord): Long =
		jdbcTemplate.queryForObject(
			"""
			INSERT INTO cell (
				lac_tac, ci, eci_nci, cgi, paragon_cgi, technology, azimuth, name,
				band_id, enb_gnb_id, location_id, mcc_mnc_id, updated_by, created_by, created_at, updated_at
			)
			VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, COALESCE(?, CURRENT_TIMESTAMP), COALESCE(?, CURRENT_TIMESTAMP))
			RETURNING id
			""".trimIndent(),
			Long::class.java,
			record.lacTac,
			record.ci,
			record.eciNci,
			record.cgi,
			record.paragonCgi,
			record.technology,
			record.azimuth,
			record.name,
			record.bandId,
			record.enbGnbId,
			record.locationId,
			record.mccMncId,
			record.updatedBy,
			record.createdBy,
			record.createdAt,
			record.updatedAt
		) ?: throw IllegalStateException("Failed to create cell")

	override fun updateCell(id: Long, record: CellWriteRecord): Int =
		jdbcTemplate.update(
			"""
			UPDATE cell
			SET lac_tac = ?,
			    ci = ?,
			    eci_nci = ?,
			    cgi = ?,
			    paragon_cgi = ?,
			    technology = ?,
			    azimuth = ?,
			    name = ?,
			    band_id = ?,
			    enb_gnb_id = ?,
			    location_id = ?,
			    mcc_mnc_id = ?,
			    updated_by = ?,
			    updated_at = COALESCE(?, updated_at)
			WHERE id = ?
			""".trimIndent(),
			record.lacTac,
			record.ci,
			record.eciNci,
			record.cgi,
			record.paragonCgi,
			record.technology,
			record.azimuth,
			record.name,
			record.bandId,
			record.enbGnbId,
			record.locationId,
			record.mccMncId,
			record.updatedBy,
			record.updatedAt,
			id
		)

	override fun upsertCellPolygon(cellId: Long, polygonWkt: String, polygonShortWkt: String) {
		val updated = jdbcTemplate.update(
			"""
			UPDATE cell_polygon
			SET polygon = ST_GeogFromText(?), polygon_short = ST_GeogFromText(?)
			WHERE cell_id = ?
			""".trimIndent(),
			polygonWkt,
			polygonShortWkt,
			cellId
		)
		if (updated == 0) {
			jdbcTemplate.update(
				"""
				INSERT INTO cell_polygon (cell_id, polygon, polygon_short)
				VALUES (?, ST_GeogFromText(?), ST_GeogFromText(?))
				""".trimIndent(),
				cellId,
				polygonWkt,
				polygonShortWkt
			)
		}
	}

	override fun deleteCell(id: Long): Int =
		jdbcTemplate.update("DELETE FROM cell WHERE id = ?", id)

	private fun ResultSet.toCellDetailsRecord(): CellDetailsRecord {
		val locationId = getNullableLong("location_id")
		val enbGnbId = getNullableLong("enb_gnb_id")
		val mccMncId = getNullableLong("mccmnc_id")
		val bandId = getNullableLong("band_id")

		return CellDetailsRecord(
			id = getLong("cell_id"),
			lacTac = getString("lac_tac"),
			ci = getString("ci"),
			eciNci = getString("eci_nci"),
			cgi = getString("cgi"),
			paragonCgi = getString("paragon_cgi"),
			technology = getInt("technology"),
			azimuth = getInt("azimuth"),
			name = getString("cell_name"),
			createdAt = getObject("created_at", OffsetDateTime::class.java),
			updatedAt = getObject("updated_at", OffsetDateTime::class.java),
			location = locationId?.let {
				CellLocationRecord(
					id = it,
					latitude = getNullableDouble("location_latitude"),
					longitude = getNullableDouble("location_longitude"),
					address = getString("location_address"),
					address1 = getString("location_address1"),
					zip4 = getNullableInt("zip4"),
					zip3 = getNullableInt("zip3"),
					postalDesignation = getString("postal_designation"),
					countyId = getNullableLong("county_id")
				)
			},
			enbGnb = enbGnbId?.let {
				CellEnbGnbRecord(
					id = it,
					enbGnb = getInt("enb_gnb")
				)
			},
			mccMnc = mccMncId?.let {
				CellMccMncRecord(
					id = it,
					type = getString("mccmnc_type"),
					mcc = getInt("mcc"),
					mnc = getInt("mnc"),
					brand = getString("brand"),
					operatorName = getString("operator_name"),
					status = getString("mccmnc_status"),
					bands = getString("mccmnc_bands"),
					notes = getString("mccmnc_notes"),
					countryId = getString("mccmnc_country_id")
				)
			},
			band = bandId?.let {
				CellBandRecord(
					id = it,
					band = getString("band_name"),
					bandwidth = getNullableDouble("bandwidth"),
					uplinkFreq = getNullableDouble("uplink_freq"),
					downlinkFreq = getNullableDouble("downlink_freq"),
					earfcn = getNullableDouble("earfcn")
				)
			},
			polygonGeoJson = getString("polygon_geojson"),
			polygonShortGeoJson = getString("polygon_short_geojson")
		)
	}

	private fun ResultSet.getNullableLong(column: String): Long? {
		val value = getLong(column)
		return if (wasNull()) null else value
	}

	private fun ResultSet.getNullableInt(column: String): Int? {
		val value = getInt(column)
		return if (wasNull()) null else value
	}

	private fun ResultSet.getNullableDouble(column: String): Double? {
		val value = getDouble(column)
		return if (wasNull()) null else value
	}

	companion object {
		private const val baseSelect = """
			SELECT
				c.id AS cell_id,
				c.lac_tac,
				c.ci,
				c.eci_nci,
				c.cgi,
				c.paragon_cgi,
				c.technology,
				c.azimuth,
				c.name AS cell_name,
				c.created_at,
				c.updated_at,
				l.id AS location_id,
				ST_Y(l.coordinates) AS location_latitude,
				ST_X(l.coordinates) AS location_longitude,
				l.address AS location_address,
				l.address1 AS location_address1,
				l.zip4,
				l.zip3,
				l.postal_designation,
				l.id_county_id AS county_id,
				e.id AS enb_gnb_id,
				e.enb_gnb,
				m.id AS mccmnc_id,
				m.type AS mccmnc_type,
				m.mcc,
				m.mnc,
				m.brand,
				m."operator" AS operator_name,
				m.status AS mccmnc_status,
				m.bands AS mccmnc_bands,
				m.notes AS mccmnc_notes,
				m.country_id AS mccmnc_country_id,
				b.id AS band_id,
				b.band AS band_name,
				b.bandwidth,
				b.uplink_freq,
				b.downlink_freq,
				b.earfcn,
				ST_AsGeoJSON(cp.polygon)::text AS polygon_geojson,
				ST_AsGeoJSON(cp.polygon_short)::text AS polygon_short_geojson
			FROM cell c
			LEFT JOIN location l ON l.id = c.location_id
			LEFT JOIN enbgnb e ON e.id = c.enb_gnb_id
			LEFT JOIN mccmnc m ON m.id = c.mcc_mnc_id
			LEFT JOIN band b ON b.id = c.band_id
			LEFT JOIN cell_polygon cp ON cp.cell_id = c.id
		"""
	}
}
