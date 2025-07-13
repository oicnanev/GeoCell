package sdato.geocell.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import sdato.geocell.domain.entities.About
import sdato.geocell.domain.entities.Author
import sdato.geocell.domain.entities.Version
import sdato.geocell.repository.SystemInfoRepository

class JdbiSystemInfoRepository(
    private val handle: Handle
) : SystemInfoRepository {
    override fun getAbout(): About {
        val authors: List<Author> = getAuthors()?.toList() ?: listOf()
        val version: Version = getVersion()

        return About(version, authors.toList())
    }

    private fun getAuthors(): MutableList<Author>? {
        return handle.createQuery("""select name, email from dbo.author;""")
            .mapTo<Author>()
            .list()
    }

    private fun getVersion(): Version {
        return handle.createQuery("""select version from dbo.version order by created_at desc limit 1""")
            .mapTo<Version>()
            .single()
    }
}