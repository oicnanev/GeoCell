package sdato.geocell.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "auth_user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 150, unique = true)
    val username: String,

    @Column(nullable = false, length = 128)
    val password: String,

    @Column
    val lastLogin: Instant? = null,

    @Column(nullable = false)
    val isSuperuser: Boolean = false,

    @Column(nullable = false, length = 150)
    val firstName: String,

    @Column(nullable = false, length = 150)
    val lastName: String,

    @Column(nullable = false, length = 254)
    val email: String,

    @Column(nullable = false)
    val isStaff: Boolean = false,

    @Column(nullable = false)
    val isActive: Boolean = true,

    @Column(nullable = false)
    val dateJoined: Instant = Instant.now()
)
