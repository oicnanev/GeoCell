package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.Band

interface BandRepository : JpaRepository<Band, Long>
