package de.bcb.transaction

import de.bcb.BcbVoter
import de.bcb.security.BcbSignatureSystem
import de.bcb.BcbVoterId

class ShowVoter(
        val pollingStationName: String,
        val encryptedVoter: String
): TransactionData {
}