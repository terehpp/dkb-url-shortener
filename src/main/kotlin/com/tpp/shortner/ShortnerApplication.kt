package com.tpp.shortner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class ShortnerApplication

fun main(args: Array<String>) {
    runApplication<ShortnerApplication>(*args)
}