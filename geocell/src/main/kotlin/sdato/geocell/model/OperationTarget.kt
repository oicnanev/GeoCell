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
@Table(name = "geocell_operationtarget")
data class OperationTarget(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "name", nullable = false, length = 256)
    val name: String,
    @ManyToOne
    @JoinColumn(name = "operation_id", nullable = false)
    val operation: Operation,
    @Column(name = "color", nullable = false, length = 7)
    val color: String,
)
