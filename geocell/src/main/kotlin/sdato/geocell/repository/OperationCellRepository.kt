package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.OperationCell

interface OperationCellRepository : JpaRepository<OperationCell, Long>
