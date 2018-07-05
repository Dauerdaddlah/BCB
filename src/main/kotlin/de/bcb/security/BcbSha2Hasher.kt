package de.bcb.security

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.MessageDigest



class BcbSha2Hasher: BcbHasher {
    companion object {
        private val charset = StandardCharsets.UTF_8
    }

    private val digest = MessageDigest.getInstance("SHA-256")

    override fun hashcode(data: String): BcbHash {
        return BcbHash(digest.digest(data.toByteArray(charset)))
    }
}