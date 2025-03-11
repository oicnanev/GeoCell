package sdato.geocell.repository

import org.springframework.data.jpa.repository.JpaRepository
import sdato.geocell.model.ContentType

interface ContentTypeRepository : JpaRepository<ContentType, Long> {
}
