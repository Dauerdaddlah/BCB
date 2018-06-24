package de.bcb.transaction

import de.bcb.ballot.BcbBallotStructure
import de.bcb.security.BcbSignature

class StartBallot(
        val countPollingStations: Int,
        val numberElections: Int,
        val ballotStructures: List<BcbBallotStructure>,
        val countVoters: Long,
        val signatureRoot: BcbSignature
): Transaction {
}