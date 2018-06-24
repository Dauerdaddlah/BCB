package de.bcb.transaction

import de.bcb.ballot.BcbBallot
import de.bcb.security.BcbSignature

class BallotsGiven(
        val pollingStationName: String,
        val number: Int,
        val ballot: List<BcbBallot>,
        val signaturePollingStation: BcbSignature
): Transaction {
}