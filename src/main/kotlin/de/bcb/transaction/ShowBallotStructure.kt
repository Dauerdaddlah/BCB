package de.bcb.transaction

import de.bcb.ballot.BcbBallotStructure
import de.bcb.security.BcbSignature

class ShowBallotStructure(
        val numberElections: Int,
        val ballotStructures: List<BcbBallotStructure>,
        val signatureRoot: BcbSignature
): Transaction {
}