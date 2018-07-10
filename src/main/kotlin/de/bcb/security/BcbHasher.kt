package de.bcb.security

interface BcbHasher {
    fun hashcode(data: String): BcbHash
}