package sdato.geocell.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "geocell_mccmnc")
data class MccMnc(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "type", length = 100)
    val type: String?,
    @Column(name = "mcc", nullable = false)
    val mcc: Int,
    @Column(name = "mnc", nullable = false)
    val mnc: Int,
    @Column(name = "brand", length = 100)
    val brand: String?,
    @Column(name = "operator", length = 200)
    val operator: String?,
    @Column(name = "status", length = 100)
    val status: String?,
    @Column(name = "bands", length = 200)
    val bands: String?,
    @Column(name = "notes", length = 300)
    val notes: String?,
    @ManyToOne
    @JoinColumn(name = "country_id")
    val country: Country?,
)
