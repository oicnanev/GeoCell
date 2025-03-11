package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.UserLocation

interface UserLocationRepository : JpaRepository<UserLocation, Long>
