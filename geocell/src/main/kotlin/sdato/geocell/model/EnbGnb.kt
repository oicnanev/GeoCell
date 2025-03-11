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
@Table(name = "geocell_enbgnb")
data class EnbGnb(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "enb_gnb", nullable = false)
    val enbGnb: Int,
    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    val location: Location,
)
