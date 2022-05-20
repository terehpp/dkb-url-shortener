package com.tpp.shortner.repository

import com.tpp.shortner.model.Url
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import java.time.Clock
import javax.persistence.EntityManager
import javax.persistence.LockModeType
import javax.persistence.TypedQuery

@Repository
class LockingUrlRepository(
    val tm: TransactionTemplate,
    val em: EntityManager,
    val urlRepository: UrlRepository,
    @Value("\${key.expiration.delay.millis}") val expirationDelay: Long
) {

    companion object {
        val logger = LoggerFactory.getLogger(LockingUrlRepository::class.java)
    }

    fun put(url: String): Long {
        val foundUrl = urlRepository.findFirstByUrl(url)
        if (foundUrl != null) {
            return foundUrl.id
        }
        return putUrl(url).id
    }

    private fun putUrl(url: String): Url {
        tm.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        tm.isolationLevel = TransactionDefinition.ISOLATION_READ_UNCOMMITTED
        val foundUrl = tm.execute {
            var query: TypedQuery<Url> = em.createQuery("SELECT u FROM Url u WHERE u.url IS NULL", Url::class.java)
            query = query.setHint("javax.persistence.lock.timeout", -2)
            query.maxResults = 1
            query.lockMode = LockModeType.PESSIMISTIC_WRITE
            val foundUrl = query.resultList.firstOrNull()?:throw RuntimeException("Limit reached")
            try {
                foundUrl.url = url
                foundUrl.expired = Clock.systemUTC().millis() + expirationDelay
                em.merge(foundUrl)
            } finally {
                em.lock(foundUrl, LockModeType.NONE)
            }
            return@execute foundUrl
        }
        return foundUrl!!
    }
}