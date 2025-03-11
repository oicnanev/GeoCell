package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.EnbGnb

interface EnbGnbRepository : JpaRepository<EnbGnb, Long>
