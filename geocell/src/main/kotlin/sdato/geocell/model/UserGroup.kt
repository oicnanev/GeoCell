package sdato.geocell.model

import jakarta.persistence.*

@Entity
@Table(name = "auth_user_groups")
data class UserGroup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    val group: Group
)
