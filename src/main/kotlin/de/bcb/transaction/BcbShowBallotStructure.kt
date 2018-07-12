package de.bcb.transaction

import de.bcb.ballot.BcbBallotsStructure

class BcbShowBallotStructure(
        val structure: BcbBallotsStructure
): BcbTransactionDataBase("BcbShowBallotStructure", structure) {
}