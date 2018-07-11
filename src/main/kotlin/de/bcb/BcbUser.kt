package de.bcb

import de.bcb.security.BcbEncryptionSystem
import de.bcb.security.BcbPrivateKey
import de.bcb.security.BcbPublicKey
import de.bcb.security.BcbSignatureSystem
import java.nio.file.Files
import java.nio.file.Paths

val nameRoot = "root"

class BcbUser(val name: String) {
    val root = name == nameRoot
    val miner: Boolean
        get() {
            return root
        }

    var encryption: BcbEncryptionSystem? = null
        private set
    var signature: BcbSignatureSystem? = null
        private set

    fun tryLoadFromFiles() {
        val path = Paths.get("keys", name)

        if (!Files.exists(path))
            return

        with(path.resolve("encryption")) {
            if (Files.exists(this)) {
                val pathPublic = resolve("public.key")

                if (Files.exists(pathPublic)) {
                    val publicKey = BcbPublicKey(Files.readAllBytes(pathPublic))
                    var privateKey: BcbPrivateKey? = null

                    val pathPrivate = resolve("private.key")
                    if (Files.exists(pathPrivate)) {
                        privateKey = BcbPrivateKey(Files.readAllBytes(pathPrivate))
                    }

                    encryption = BcbEncryptionSystem(publicKey, privateKey)
                }
            }
        }

        with(path.resolve("signature")) {
            if (Files.exists(this)) {
                val pathPublic = resolve("public.key")

                if (Files.exists(pathPublic)) {
                    val publicKey = BcbPublicKey(Files.readAllBytes(pathPublic))
                    var privateKey: BcbPrivateKey? = null

                    val pathPrivate = resolve("private.key")
                    if (Files.exists(pathPrivate)) {
                        privateKey = BcbPrivateKey(Files.readAllBytes(pathPrivate))
                    }

                    signature = BcbSignatureSystem(publicKey, privateKey)
                }
            }
        }
    }
}