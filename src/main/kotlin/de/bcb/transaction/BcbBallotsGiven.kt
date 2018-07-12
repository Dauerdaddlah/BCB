package de.bcb.transaction

import de.bcb.ballot.BcbBallot

class BcbBallotsGiven(
        val pollingStationName: String,
        val ballot: List<BcbBallot>
): BcbTransactionDataBase("BcbBallotsGiven", pollingStationName, ballot) {
}