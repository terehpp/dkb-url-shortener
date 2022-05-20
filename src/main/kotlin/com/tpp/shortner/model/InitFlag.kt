package com.tpp.shortner.model

import java.time.Clock
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

const val INIT_FLAG_DEFAULT_ID = "Initialization"

@Entity
data class InitFlag(
    @Id
    var id: String = INIT_FLAG_DEFAULT_ID,
    @Column
    var start: Long = Clock.systemUTC().millis(),
    @Column
    var completed: Boolean = false
): java.io.Serializable