package com.tpp.shortner.repository

import com.tpp.shortner.model.Url
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UrlRepository: JpaRepository<Url, Long> {
    fun findFirstByUrl(url: String): Url?

    @Modifying
    @Query("UPDATE Url u SET u.url = null WHERE u.expired < :expired")
    fun clearExpired(@Param("expired") expired: Long)
}