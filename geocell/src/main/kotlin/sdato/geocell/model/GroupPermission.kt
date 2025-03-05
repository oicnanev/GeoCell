package sdato.geocell.model

import jakarta.persistence.*

@Entity
@Table(name = "auth_group_permissions")
data class GroupPermission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    val group: Group,

    @ManyToOne
    @JoinColumn(name = "permission_id", nullable = false)
    val permission: Permission
)
