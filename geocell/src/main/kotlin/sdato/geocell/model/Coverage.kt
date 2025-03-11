package sdato.geocell.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "geocell_coverage")
data class Coverage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "signal_strength")
    val signalStrength: Int?,
    @Column(name = "timestamp", nullable = false)
    val timestamp: Instant,
    @ManyToOne
    @JoinColumn(name = "cell_id", nullable = false)
    val cell: Cell,
    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    val location: Location,
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User?,
)
