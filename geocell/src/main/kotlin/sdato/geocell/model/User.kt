package sdato.geocell.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "auth_user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "username", nullable = false, length = 150, unique = true)
    val username: String,

    @Column(name = "password", nullable = false, length = 128)
    val password: String,

    @Column(name = "last_login")
    val lastLogin: Instant? = null,

    @Column(name = "is_superuser", nullable = false)
    val isSuperuser: Boolean = false,

    @Column(name = "first_name", nullable = false, length = 150)
    val firstName: String,

    @Column(name = "last_name", nullable = false, length = 150)
    val lastName: String,

    @Column(name = "email", nullable = false, length = 254)
    val email: String,

    @Column(name = "is_staff", nullable = false)
    val isStaff: Boolean = false,

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,

    @Column(name = "date_joined", nullable = false)
    val dateJoined: Instant = Instant.now(),

    @OneToMany(mappedBy = "user")
    val adminLogs: List<AdminLog> = emptyList()
)
