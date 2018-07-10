package de.bcb.security

import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Cipher

class BcbEncryptionSystem(
    val publicKey: BcbPublicKey,
    val privateKey: BcbPrivateKey?
) {
    companion object {
        private val charset = StandardCharsets.UTF_8
    }

    // verschlüsseln
    fun encrypt(data: String): String {
        return with(Cipher.getInstance("RSA"))
        {
            init(Cipher.ENCRYPT_MODE, publicKey.key)
            String(
                Base64.getEncoder().encode(
                    doFinal(
                        data.toByteArray(charset))),
                charset)
        }
    }

    // entschlüsseln
    fun decrypt(data: String): String {
        if(privateKey == null) {
            TODO()
        }
        return with(Cipher.getInstance("RSA"))
        {
            init(Cipher.DECRYPT_MODE, privateKey.key)
            String(
                    doFinal(
                        Base64.getDecoder().decode(
                            data.toByteArray(charset))),
                    charset)
        }
    }
}