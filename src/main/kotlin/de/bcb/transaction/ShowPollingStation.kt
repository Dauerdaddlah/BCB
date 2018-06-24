package de.bcb.transaction

import de.bcb.security.BcbSignature
import java.security.PublicKey

class ShowPollingStation(
        val name: String,
        val signatureKey: PublicKey,
        val encryptionKey: PublicKey,
        val rootSignature: BcbSignature,
        val pollingStationSignature: BcbSignature
): Transaction {
}