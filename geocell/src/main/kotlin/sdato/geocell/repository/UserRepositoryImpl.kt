package sdato.geocell.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Repository
import sdato.geocell.model.Group
import sdato.geocell.model.User

@Repository
class UserRepositoryImpl(private val em: EntityManager) : UserRepositoryCustom {
    override fun searchUsersWithFilters(
        username: String?,
        email: String?,
        isActive: Boolean?,
        groupIds: List<Long>?,
    ): List<User> {
        val cb = em.criteriaBuilder
        val query = cb.createQuery(User::class.java)
        val root = query.from(User::class.java)

        val predicates = mutableListOf<Predicate>()

        username?.let {
            predicates.add(cb.like(cb.lower(root.get("username")), "%${it.lowercase()}%"))
        }

        email?.let {
            predicates.add(cb.like(cb.lower(root.get("email")), "%${it.lowercase()}%"))
        }

        isActive?.let {
            predicates.add(cb.equal(root.get<Boolean>("isActive"), it))
        }

        groupIds?.let { ids ->
            if (ids.isNotEmpty()) {
                val groupJoin: Join<User, Group> = root.join<User, Group>("groups")
                predicates.add(groupJoin.get<Any>("id").`in`(ids))
            }
        }

        query.where(*predicates.toTypedArray())
        return em.createQuery(query).resultList
    }
}
