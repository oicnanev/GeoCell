package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.County

interface CountyRepository : JpaRepository<County, Long>
