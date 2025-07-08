package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdato.geocell.model.OperationCell

@Repository
interface OperationCellRepository : JpaRepository<OperationCell, Long>
