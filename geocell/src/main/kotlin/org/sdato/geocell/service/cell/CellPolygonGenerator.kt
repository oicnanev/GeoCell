package org.sdato.geocell.service.cell

import org.springframework.stereotype.Component
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Component
class CellPolygonGenerator {

	fun buildDefaultPolygonWkt(latitude: Double, longitude: Double, direction: Int): String {
		val points = calculatePolygon(latitude, longitude, direction, radius = 500.0, altitude = 20.0, amplitude = 110.0)
		return toPolygonZWkt(points)
	}

	fun buildShortPolygonWkt(latitude: Double, longitude: Double, direction: Int, technology: Int): String {
		val radius = when (technology) {
			2 -> 300.0
			3 -> 275.0
			4 -> 250.0
			5 -> 225.0
			else -> 200.0
		}
		val altitude = when (technology) {
			2 -> 20.0
			3 -> 25.0
			4 -> 30.0
			5 -> 35.0
			else -> 40.0
		}
		val points = calculatePolygon(latitude, longitude, direction, radius = radius, altitude = altitude, amplitude = 110.0)
		return toPolygonZWkt(points)
	}

	private fun calculatePolygon(
		latitude: Double,
		longitude: Double,
		direction: Int,
		radius: Double,
		altitude: Double,
		amplitude: Double
	): List<Point3D> {
		val nSides = 360
		val circlePoints = sphericalPoints(longitude, latitude, radius, altitude, nSides, offsetDegrees = 0.0)
		if (amplitude.roundToInt() == 360) {
			return circlePoints
		}

		val selected = mutableListOf<Point3D>()
		val origin = Point3D(longitude, latitude, altitude)
		selected.add(origin)

		val reverseSelection = mutableListOf<Point3D>()
		val half = (amplitude / 2.0).roundToInt()
		for (i in 0 until half) {
			val idx = (direction - i).floorMod(nSides)
			reverseSelection.add(circlePoints[idx])
		}
		selected.addAll(reverseSelection.asReversed())

		for (i in 0 until half) {
			val idx = (direction + i).floorMod(nSides)
			selected.add(circlePoints[idx])
		}

		selected.add(origin)
		return selected
	}

	private fun sphericalPoints(
		longitude: Double,
		latitude: Double,
		meters: Double,
		altitude: Double,
		sides: Int,
		offsetDegrees: Double
	): List<Point3D> {
		val rad = PI / 180.0
		val offsetRadians = offsetDegrees * rad
		val r = meters / (cos(latitude * rad) * 10_000_000.0)

		val vec = toCartesian(longitude * rad, latitude * rad)
		val pt = toCartesian(longitude * rad + r, latitude * rad)
		val points = mutableListOf<Point3D>()

		for (i in 0 until sides) {
			val rotated = rotatePoint(vec, pt, offsetRadians + ((2.0 * PI) / sides) * i)
			points.add(toEarth(rotated, altitude))
		}
		points.add(points.first())

		return points
	}

	private fun toEarth(cartesian: Triple<Double, Double, Double>, altitude: Double): Point3D {
		val longitude = atan2(cartesian.second, cartesian.first)
		val coLatitude = acos(cartesian.third)
		val latitude = PI / 2.0 - coLatitude
		val deg = 180.0 / PI

		return Point3D(
			longitude = longitude * deg,
			latitude = latitude * deg,
			altitude = altitude
		)
	}

	private fun toCartesian(longitudeRad: Double, latitudeRad: Double): Triple<Double, Double, Double> {
		val phi = PI / 2.0 - latitudeRad
		return Triple(
			cos(longitudeRad) * sin(phi),
			sin(longitudeRad) * sin(phi),
			cos(phi)
		)
	}

	private fun rotatePoint(
		vector: Triple<Double, Double, Double>,
		point: Triple<Double, Double, Double>,
		phi: Double
	): Triple<Double, Double, Double> {
		val u = vector.first
		val v = vector.second
		val w = vector.third
		val x = point.first
		val y = point.second
		val z = point.third

		val a = u * x + v * y + w * z
		val d = cos(phi)
		val e = sin(phi)

		return Triple(
			a * u + (x - a * u) * d + (v * z - w * y) * e,
			a * v + (y - a * v) * d + (w * x - u * z) * e,
			a * w + (z - a * w) * d + (u * y - v * x) * e
		)
	}

	private fun toPolygonZWkt(points: List<Point3D>): String {
		require(points.size >= 4) { "Need at least 4 points to build a polygon" }
		val coords = points.joinToString(",") { "${it.longitude} ${it.latitude} ${it.altitude}" }
		return "SRID=4326;POLYGON Z(($coords))"
	}

	private fun Int.floorMod(modulus: Int): Int = ((this % modulus) + modulus) % modulus

	private data class Point3D(
		val longitude: Double,
		val latitude: Double,
		val altitude: Double
	)
}
