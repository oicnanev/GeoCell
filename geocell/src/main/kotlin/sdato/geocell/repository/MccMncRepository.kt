package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.MccMnc

interface MccMncRepository : JpaRepository<MccMnc, Long>
