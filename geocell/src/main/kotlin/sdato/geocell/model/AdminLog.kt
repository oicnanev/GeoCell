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
@Table(name = "admin_log")
data class AdminLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "action_time", nullable = false)
    val actionTime: Instant,
    @Column(name = "object_id")
    val objectId: String?,
    @Column(name = "object_repr", nullable = false, length = 200)
    val objectRepr: String,
    @Column(name = "action_flag", nullable = false)
    val actionFlag: Short,
    @Column(name = "change_message", nullable = false)
    val changeMessage: String,
    @ManyToOne
    @JoinColumn(name = "content_type_id")
    val contentType: ContentType?,
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
)
