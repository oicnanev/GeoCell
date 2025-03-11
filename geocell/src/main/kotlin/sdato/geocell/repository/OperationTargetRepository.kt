package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.OperationTarget

interface OperationTargetRepository : JpaRepository<OperationTarget, Long>
