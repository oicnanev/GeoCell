package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdato.geocell.model.District

@Repository
interface DistrictRepository : JpaRepository<District, Long>
