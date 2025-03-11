package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.Group

interface GroupRepository : JpaRepository<Group, Long>
