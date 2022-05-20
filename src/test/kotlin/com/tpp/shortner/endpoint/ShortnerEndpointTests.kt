package com.tpp.shortner.endpoint

import org.apache.http.impl.client.HttpClientBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShortnerEndpointTests {

    @LocalServerPort val port: Int = 0

    @Test
    fun baseCase() {
        val client = HttpClientBuilder.create().disableRedirectHandling().build()
        val restTemplate = RestTemplate(HttpComponentsClientHttpRequestFactory(client))
        val redirectUrl = "http://www.google.com/"
        val entity = HttpEntity<String>(redirectUrl)
        val resp = restTemplate!!
            .exchange("http://localhost:$port/make", HttpMethod.PUT, entity, String::class.java)
        val shortUrl = resp!!.body!!
        Assertions.assertTrue(shortUrl.isNotBlank())

        val redirect = restTemplate.getForEntity("http://localhost:$port/$shortUrl", String::class.java)
        Assertions.assertEquals(HttpStatus.FOUND, redirect.statusCode)
        Assertions.assertEquals(redirectUrl, redirect.headers.location.toString())
    }

    @Test
    fun invalidUrlTest() {
        val restTemplate = TestRestTemplate()
        val redirectUrl = "I am not url"
        val entity = HttpEntity<String>(redirectUrl)
        val resp = restTemplate!!
            .exchange("http://localhost:$port/make", HttpMethod.PUT, entity, String::class.java)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode)
    }
}