package sdato.geocell.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.locationtech.jts.geom.Point

@Entity
@Table(name = "geocell_location")
data class Location(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "coordinates", columnDefinition = "geometry(Point,4326)")
    val coordinates: Point?,
    @Column(name = "address", length = 100)
    val address: String?,
    @Column(name = "address1", length = 100)
    val address1: String?,
    @Column(name = "zip4", nullable = false)
    val zip4: Int,
    @Column(name = "zip3", nullable = false)
    val zip3: Int,
    @Column(name = "postal_designation", length = 100)
    val postalDesignation: String?,
    @ManyToOne
    @JoinColumn(name = "id_county_id")
    val county: County?,
)
