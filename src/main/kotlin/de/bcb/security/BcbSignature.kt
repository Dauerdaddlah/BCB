package de.bcb.security

import java.nio.charset.StandardCharsets
import java.security.Signature
import java.util.*

class BcbSignature(
        val publicKey: BcbPublicKey,
        val privateKey: BcbPrivateKey?
) {
    companion object {
        private val charset = StandardCharsets.UTF_8
    }

    init {

    }

    fun sign(data: String): String {
        if(privateKey == null) {
            TODO()
        }

        val sign = with(Signature.getInstance("SHA1withRSA")) {
            initSign(privateKey.key)
            update(data.toByteArray(charset))
            sign()
        }


        return String(
                Base64.getEncoder().encode(
                        sign),
                charset)
    }

    fun verifySignature(data: String, signature: String): Boolean {
        return with(Signature.getInstance("SHA1withRSA"))
        {
            initVerify(publicKey.key)
            update(data.toByteArray(charset))
            verify(
                    Base64.getDecoder().decode(
                            signature.toByteArray(charset)))
        }
    }
}