package com.tpp.shortner.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootTest
@EnableTransactionManagement
class UrlServiceTests(@Autowired val urlService: UrlService) {
    @Test
    fun dummyTest() {
        Assertions.assertEquals("something", urlService.get(urlService.put("something")))
    }
}