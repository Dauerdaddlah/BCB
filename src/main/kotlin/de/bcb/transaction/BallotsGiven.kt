package de.bcb.transaction

import de.bcb.ballot.BcbBallot
import de.bcb.security.BcbSignatureSystem

class BallotsGiven(
        val pollingStationName: String,
        val number: Int,
        val ballot: List<BcbBallot>,
        val signaturePollingStation: BcbSignatureSystem
): TransactionData {
}