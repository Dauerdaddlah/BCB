package de.bcb.transaction

import de.bcb.security.BcbSignatureSystem
import de.bcb.BcbVoterId

class VoterUsed(
        val pollingStationName: String,
        val voterId: BcbVoterId,
        val voted: Boolean,
        val signaturePollingStation: BcbSignatureSystem
): TransactionData {
}