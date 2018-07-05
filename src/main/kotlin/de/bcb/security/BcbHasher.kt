package de.bcb.security

var defaultHasher : BcbHasher = BcbSha2Hasher()

interface BcbHasher {
    fun hashcode(data: String): BcbHash
}