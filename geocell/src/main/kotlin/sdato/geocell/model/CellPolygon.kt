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
@Table(name = "geocell_cellpolygon")
data class CellPolygon(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "polygon", columnDefinition = "geometry(PolygonZ,4326)")
    val polygon: Polygon?,
    @Column(name = "polygon_short", columnDefinition = "geometry(PolygonZ,4326)")
    val polygonShort: Polygon?,
    @ManyToOne
    @JoinColumn(name = "cell_id")
    val cell: Cell?,
)
