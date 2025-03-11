package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.UserPermission

interface UserPermissionRepository : JpaRepository<UserPermission, Long>
