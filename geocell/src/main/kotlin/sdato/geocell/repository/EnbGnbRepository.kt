package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdato.geocell.model.EnbGnb

@Repository
interface EnbGnbRepository : JpaRepository<EnbGnb, Long>
