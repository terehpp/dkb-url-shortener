package com.tpp.shortner.model

import org.springframework.data.domain.Persistable
import javax.persistence.*

@Entity
@Table(indexes = [
    Index(name = "url_url_idx", columnList = "url"),
    Index(name = "url_expired", columnList = "expired"),
])
data class Url(
    @Id
    var id: Long,
    @Column
    var url: String? = null,
    @Column
    var expired: Long? = null,
): java.io.Serializable, Persistable<Long> {
    override fun getId(): Long? {
        return id;
    }

    override fun isNew(): Boolean {
        return url?.isEmpty() ?: true
    }
}