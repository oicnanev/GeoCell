package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository

interface CoverageRepository : JpaRepository<CountyRepository, Long>
