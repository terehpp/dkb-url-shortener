package com.tpp.shortner.service

import com.tpp.shortner.repository.LockingUrlRepository
import com.tpp.shortner.repository.UrlRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import javax.transaction.Transactional


@Service
class UrlService(val lockingUrlRepository: LockingUrlRepository,
                 val urlRepository: UrlRepository,
                 val keyConverter: KeyConverter
) {

    fun put(url: String): String {
        val id = lockingUrlRepository.put(url)
        return keyConverter.convert(id.toULong())
    }

    fun get(shortUrl: String): String? {
        val id = keyConverter.convert(shortUrl).toLong()
        return urlRepository.findById(id)
            .map { u -> u.url }.orElse(null)
    }

    @Scheduled(fixedDelayString = "\${key.expired.clear.delay.millis}")
    @Transactional
    fun clearExpired() {
        urlRepository.clearExpired(Clock.systemUTC().millis())
    }
}