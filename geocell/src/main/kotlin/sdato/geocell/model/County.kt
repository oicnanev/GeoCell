package sdato.geocell.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.locationtech.jts.geom.Polygon

@Entity
@Table(name = "geocell_county")
data class County(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "id_county", nullable = false, length = 20)
    val idCounty: String,
    @Column(name = "county", nullable = false, length = 100)
    val county: String,
    @Column(name = "polygon", columnDefinition = "geometry(Polygon,4326)")
    val polygon: Polygon?,
    @ManyToOne
    @JoinColumn(name = "district_id")
    val district: District?,
)
