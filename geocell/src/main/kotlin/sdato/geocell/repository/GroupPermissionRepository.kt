package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.GroupPermission

interface GroupPermissionRepository : JpaRepository<GroupPermission, Long>
