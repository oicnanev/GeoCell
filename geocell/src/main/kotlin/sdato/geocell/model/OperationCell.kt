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
@Table(name = "geocell_operationcell")
data class OperationCell(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    @ManyToOne
    @JoinColumn(name = "cell_id", nullable = false)
    val cell: Cell,
    @ManyToOne
    @JoinColumn(name = "target_id", nullable = false)
    val target: OperationTarget,
    @Column(name = "timestamp", nullable = false)
    val timestamp: Instant,
    @Column(name = "user_time")
    val userTime: Instant?,
)
