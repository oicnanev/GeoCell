package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.District

interface DistrictRepository : JpaRepository<District, Long>
