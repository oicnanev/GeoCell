package sdato.geocell.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "geocell_band")
data class Band(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "band", length = 50)
    val band: String?,
    @Column(name = "bandwidth")
    val bandwidth: Double?,
    @Column(name = "uplink_freq")
    val uplinkFreq: Double?,
    @Column(name = "downlink_freq")
    val downlinkFreq: Double?,
    @Column(name = "earfcn")
    val earfcn: Double?,
)
