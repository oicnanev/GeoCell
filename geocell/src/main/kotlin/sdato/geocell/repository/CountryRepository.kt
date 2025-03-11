package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.Country

interface CountryRepository : JpaRepository<Country, Long>
