package de.bcb.security

import java.nio.charset.StandardCharsets
import java.util.*

class BcbHash(
        private val hash: ByteArray
) {
    companion object {
        private val charset = StandardCharsets.UTF_8
    }

    override fun toString(): String {
        return String(Base64.getEncoder().encode(hash), charset)
    }

    override fun equals(other: Any?): Boolean {
        return other is BcbHash && Arrays.equals(hash, other.hash)
    }
}