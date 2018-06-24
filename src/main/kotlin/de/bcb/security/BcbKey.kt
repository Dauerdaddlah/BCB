package de.bcb.security

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*

abstract class BcbKey(
        open val key: Key
): Key by key {
    abstract fun toBytes(): ByteArray

    fun toString(charset: Charset = StandardCharsets.UTF_8): String {
        return String(Base64.getEncoder().encode(toBytes()))
    }
}