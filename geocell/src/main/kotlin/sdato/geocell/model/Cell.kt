package sdato.geocell.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "geocell_cell")
data class Cell(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "lac_tac", nullable = false, length = 50)
    val lacTac: String,
    @Column(name = "ci", length = 20)
    val ci: String?,
    @Column(name = "eci_nci", length = 20)
    val eciNci: String?,
    @Column(name = "cgi", length = 30)
    val cgi: String?,
    @Column(name = "paragon_cgi", length = 100)
    val paragonCgi: String?,
    @Column(name = "technology", nullable = false)
    val technology: Int,
    @Column(name = "direction", nullable = false)
    val direction: Int,
    @Column(name = "name", length = 200)
    val name: String?,
    @Column(name = "created", nullable = false)
    val created: LocalDate,
    @Column(name = "modified", nullable = false)
    val modified: LocalDate,
    @ManyToOne
    @JoinColumn(name = "band_id")
    val band: Band?,
    @ManyToOne
    @JoinColumn(name = "enb_gnb_id")
    val enbGnb: EnbGnb?,
    @ManyToOne
    @JoinColumn(name = "location_id")
    val location: Location?,
    @ManyToOne
    @JoinColumn(name = "mcc_mnc_id")
    val mccMnc: MccMnc?,
    @ManyToOne
    @JoinColumn(name = "modifier_id")
    val modifier: User?,
    @ManyToOne
    @JoinColumn(name = "owner_id")
    val owner: User?,
)
