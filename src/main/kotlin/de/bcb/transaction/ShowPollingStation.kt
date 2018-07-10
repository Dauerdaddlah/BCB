package de.bcb.transaction

import de.bcb.security.BcbPublicKey
import de.bcb.security.BcbSignatureSystem
import java.security.PublicKey

class ShowPollingStation(
        val name: String,
        val signatureKey: BcbPublicKey,
        val encryptionKey: BcbPublicKey
): TransactionData {
}