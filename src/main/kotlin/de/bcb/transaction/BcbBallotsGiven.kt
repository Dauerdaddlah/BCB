package de.bcb.transaction

import de.bcb.ballot.BcbBallots

class BcbBallotsGiven(
        val pollingStationName: String,
        val ballots: BcbBallots
): BcbTransactionDataBase("BcbBallotsGiven", pollingStationName, ballots)