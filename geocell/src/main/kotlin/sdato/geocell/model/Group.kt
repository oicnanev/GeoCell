package sdato.geocell.model

import jakarta.persistence.*

@Entity
@Table(name = "auth_group")
data class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, length = 150)
    val name: String
)
