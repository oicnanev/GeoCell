package sdato.geocell.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

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
    val permission: Permission,
)
