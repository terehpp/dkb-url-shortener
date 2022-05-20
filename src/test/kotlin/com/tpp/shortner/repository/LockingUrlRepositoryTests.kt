package com.tpp.shortner.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

/**
 * ATTENTION:
 * It's performance test.
 * Also, it uses real database look into application-huge-db.properties
 * That's why it was disabled for automatic start.
 */
//@SpringBootTest
@EnableTransactionManagement
@TestPropertySource(locations = ["classpath:application-huge-db.properties"])
class LockingUrlRepositoryTests(
    @Autowired val tx: TransactionTemplate,
    @Autowired val lockingUrlRepository: LockingUrlRepository,
    @Value("\${key.start}") val start: Long,
    @Value("\${key.end}") val end: Long,
) {

//    @Test
    fun concurrentPut() {
        val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        val futures = ArrayList<CompletableFuture<Long>>()

        var sum = 0L
        for(i in start..end) {
            sum += i
            val future = CompletableFuture<Long>()
            pool.submit { future.complete(lockingUrlRepository.put(i.toString())) }
            futures.add(future)
        }

        val count = futures.map { x -> x.get() as Long }.toSet().sum()
        Assertions.assertEquals(sum, count)
    }
}