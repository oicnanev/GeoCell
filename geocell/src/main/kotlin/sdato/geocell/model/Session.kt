package sdato.geocell.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "session")
data class Session(
    @Id
    @Column(name = "session_key", length = 40, nullable = false)
    val sessionKey: String,
    @Column(name = "session_data", nullable = false)
    val sessionData: String,
    @Column(name = "expire_date", nullable = false)
    val expireDate: Instant,
)
