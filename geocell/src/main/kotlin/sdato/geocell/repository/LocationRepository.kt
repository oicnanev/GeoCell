package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.Location

interface LocationRepository : JpaRepository<Location, Long>
