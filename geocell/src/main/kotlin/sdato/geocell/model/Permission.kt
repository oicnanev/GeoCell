package sdato.geocell.model

import jakarta.persistence.*

@Entity
@Table(name = "auth_permission")
data class Permission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 255)
    val name: String,

    @Column(nullable = false, unique = true, length = 100)
    val codename: String
)
