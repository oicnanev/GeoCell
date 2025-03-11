package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.Permission

interface PermissionRepository : JpaRepository<Permission, Long>
