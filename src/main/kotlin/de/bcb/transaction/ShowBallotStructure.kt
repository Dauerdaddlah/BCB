package de.bcb.transaction

import de.bcb.ballot.BcbBallotStructure
import de.bcb.ballot.BcbBallotsStructure
import de.bcb.security.BcbSignatureSystem

class ShowBallotStructure(
        val structure: BcbBallotsStructure
): TransactionData {
}