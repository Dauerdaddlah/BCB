package de.bcb

import java.time.LocalDateTime

typealias BcbTimestamp = LocalDateTime
typealias BcbVersion = Int

typealias BcbVoterId = Any

// simulates that we create a coroutine
fun launch(block: () -> Unit) {
    val t = Thread(block)
    t.start()
}