package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdato.geocell.model.County

@Repository
interface CountyRepository : JpaRepository<County, Long>
