package sdato.geocell.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.locationtech.jts.geom.Polygon

@Entity
@Table(name = "geocell_country")
data class Country(
    @Id
    @Column(name = "name", nullable = false, length = 100)
    val name: String,
    @Column(name = "code", length = 4)
    val code: String?,
    @Column(name = "polygon", columnDefinition = "geometry(Polygon,4326)")
    val polygon: Polygon?,
    @Column(name = "flag", length = 100)
    val flag: String?,
)
