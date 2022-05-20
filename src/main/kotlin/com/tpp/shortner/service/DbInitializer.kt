package com.tpp.shortner.service

import com.tpp.shortner.model.INIT_FLAG_DEFAULT_ID
import com.tpp.shortner.model.InitFlag
import com.tpp.shortner.model.Url
import com.tpp.shortner.repository.UrlRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.annotation.DependsOn
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import javax.persistence.EntityManager

@Service
class DbInitializer(
    val tm: TransactionTemplate,
    val em: EntityManager,
    val urlRepository: UrlRepository,
    @Value("\${key.start}") var start: Long,
    @Value("\${key.end}") var end: Long,
    @Value("\${key.init.batch}") var batch: Int
) {

    companion object {
        val logger = LoggerFactory.getLogger(DbInitializer::class.java)
    }

    fun init() {
        if (start > end) {
            logger.error("""Invalid key range $start - $end""")
            throw RuntimeException("Invalid key range configuration")
        }
        val flag = tm.execute { em.find(InitFlag::class.java, INIT_FLAG_DEFAULT_ID) }
        if (flag != null) {
            if (flag.completed) {
                return
            }
        } else {
            logger.info("Try to lock database for initialization")
            val flag = takeFlag()
            logger.info("Start filling Url table with data")
            fillDb()
            logger.info("Complete filling Url table with data")
            freeFlag(flag)
            return
        }
    }

    private fun freeFlag(flag: InitFlag) {
        flag.completed = true
        tm.execute {
            em.merge(flag)
            em.flush()
        }
    }

    private fun takeFlag(): InitFlag {
        val flag = InitFlag()
        tm.execute {
            em.persist(flag)
            em.flush()
        }
        return flag
    }

    fun fillDb() {
        try {
            for (chunk in (start..end).chunked(batch)) {
                val urls = chunk.map {
                    Url(id = it)
                }.asIterable()
                tm.execute {
                    urlRepository.saveAllAndFlush(urls)
                }
            }
        } catch (e: Exception) {
            logger.error("Cannot initialize Url", e)
            throw RuntimeException("Critical error while db initialization")
        }
    }

    @EventListener(ApplicationStartedEvent::class)
    fun initDb() {
        init()
    }
}