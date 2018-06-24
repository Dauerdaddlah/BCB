package de.bcb.security

import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

class BcbPrivateKey(
        override val key: PrivateKey
): BcbKey(key) {

    companion object {
        private fun privateKeyFromString(s: String): PrivateKey {
            return privateKeyFromBytes(Base64.getDecoder().decode(s.toByteArray(StandardCharsets.UTF_8)))
        }

        private fun privateKeyFromBytes(bytes: ByteArray): PrivateKey {
            val spec = PKCS8EncodedKeySpec(bytes)
            val kf = KeyFactory.getInstance("RSA")
            return kf.generatePrivate(spec)
        }
    }

    constructor(keyString: String): this(privateKeyFromString(keyString))
    constructor(keyBytes: ByteArray): this(privateKeyFromBytes(keyBytes))

    override fun toBytes(): ByteArray {
        val pkcs8EncodedKeySpec = PKCS8EncodedKeySpec(key.encoded)
        return pkcs8EncodedKeySpec.encoded
    }
}