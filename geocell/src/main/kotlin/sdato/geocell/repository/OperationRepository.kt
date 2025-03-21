package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.Operation

interface OperationRepository : JpaRepository<Operation, Long>
