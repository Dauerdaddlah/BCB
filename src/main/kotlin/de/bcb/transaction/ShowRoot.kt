package de.bcb.transaction

import de.bcb.security.BcbPublicKey
import de.bcb.security.BcbSignatureSystem
import java.security.PublicKey

class ShowRoot(
    val signatureKey: BcbPublicKey,
    val encryptionKey: BcbPublicKey
): TransactionData {

}