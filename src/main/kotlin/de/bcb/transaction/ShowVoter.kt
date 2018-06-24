package de.bcb.transaction

import de.bcb.security.BcbSignature
import de.bcb.BcbVoterId

class ShowVoter(
        val pollingStationName: String,
        val voterId: BcbVoterId,
        val signatureRoot: BcbSignature,
        val signaturePollingStation: BcbSignature
): Transaction {
}