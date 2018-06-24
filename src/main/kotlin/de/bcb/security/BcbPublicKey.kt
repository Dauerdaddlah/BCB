package de.bcb.security

import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*

class BcbPublicKey(
        override val key: PublicKey
): BcbKey(key) {

    companion object {
        private fun publicKeyFromString(s: String): PublicKey {
            return publicKeyFromBytes(Base64.getDecoder().decode(s.toByteArray(StandardCharsets.UTF_8)))
        }

        private fun publicKeyFromBytes(bytes: ByteArray): PublicKey {
            val spec = X509EncodedKeySpec(bytes)
            val kf = KeyFactory.getInstance("RSA")
            return kf.generatePublic(spec)
        }
    }

    constructor(keyString: String): this(publicKeyFromString(keyString))
    constructor(keyBytes: ByteArray): this(publicKeyFromBytes(keyBytes))

    override fun toBytes(): ByteArray {
        val x509EncodedKeySpec = X509EncodedKeySpec(key.encoded)
        return x509EncodedKeySpec.encoded
    }
}