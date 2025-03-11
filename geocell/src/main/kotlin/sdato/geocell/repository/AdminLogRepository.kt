package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.AdminLog

interface AdminLogRepository : JpaRepository<AdminLog, Long> {
}
