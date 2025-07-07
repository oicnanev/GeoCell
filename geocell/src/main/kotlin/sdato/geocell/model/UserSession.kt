package sdato.geocell.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "user_sessions")
data class UserSession(
    @Id
    @Column(length = 36)
    val id: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    @Column(nullable = false, unique = true)
    val sessionToken: String,
    val ipAddress: String? = null,
    val userAgent: String? = null,
    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),
    @Column(nullable = false)
    val expiresAt: Instant,
    @Column(nullable = false)
    var isValid: Boolean = true,
) {
    fun isExpired(): Boolean = Instant.now().isAfter(expiresAt)
}
