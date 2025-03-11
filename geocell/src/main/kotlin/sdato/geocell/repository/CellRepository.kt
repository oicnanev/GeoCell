package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.Cell

interface CellRepository : JpaRepository<Cell, Long>
