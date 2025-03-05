package sdato.geocell.model

import jakarta.persistence.*

@Entity
@Table(name = "auth_user_user_permissions")
data class UserPermission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne
    @JoinColumn(name = "permission_id", nullable = false)
    val permission: Permission
)
