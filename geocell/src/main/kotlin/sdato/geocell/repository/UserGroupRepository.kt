package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.UserGroup

interface UserGroupRepository : JpaRepository<UserGroup, Long>
