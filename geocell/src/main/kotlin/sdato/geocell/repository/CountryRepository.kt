package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdato.geocell.model.Country

@Repository
interface CountryRepository : JpaRepository<Country, Long>
