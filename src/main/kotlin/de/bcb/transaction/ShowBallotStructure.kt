package de.bcb.transaction

import de.bcb.ballot.BcbBallotStructure
import de.bcb.security.BcbSignatureSystem

class ShowBallotStructure(
        val numberElections: Int,
        val ballotStructures: List<BcbBallotStructure>
): TransactionData {
}