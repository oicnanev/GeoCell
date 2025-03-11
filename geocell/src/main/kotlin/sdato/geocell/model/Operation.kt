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
@Table(name = "geocell_operation")
data class Operation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "name", nullable = false, length = 64)
    val name: String,
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    val group: Group,
    @Column(name = "active", nullable = false)
    val active: Boolean = false,
    @Column(name = "modified", nullable = false)
    val modified: Instant = Instant.now(),
)
