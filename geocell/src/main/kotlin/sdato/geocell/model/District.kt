package sdato.geocell.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.locationtech.jts.geom.Polygon

@Entity
@Table(name = "geocell_district")
data class District(
    @Id
    @Column(name = "id", nullable = false, length = 20)
    val id: String,
    @Column(name = "district", nullable = false, length = 100)
    val district: String,
    @Column(name = "polygon", columnDefinition = "geometry(Polygon,4326)")
    val polygon: Polygon?,
    @ManyToOne
    @JoinColumn(name = "country_id")
    val country: Country?,
)
