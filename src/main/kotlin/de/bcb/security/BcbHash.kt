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
        return when {
            hash.isEmpty() -> "0"
            else -> String(Base64.getEncoder().encode(hash), charset)
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is BcbHash && Arrays.equals(hash, other.hash)
    }

    fun toLong(): Long {
        var l: Long = 0
        if(!hash.isEmpty()) {
            l = l.or(hash.last().toLong().and(0xff))
        }
        if(hash.size > 1) {
            l = l.or(hash[hash.size - 2].toLong().and(0xff).shl(8))
        }
        if(hash.size > 2) {
            l = l.or(hash[hash.size - 3].toLong().and(0xff).shl(16))
        }
        if(hash.size > 3) {
            l = l.or(hash[hash.size - 4].toLong().and(0xff).shl(32))
        }
        return l
    }
}