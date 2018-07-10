package de.bcb.transaction

import de.bcb.ballot.BcbBallotStructure
import de.bcb.security.BcbSignatureSystem

class StartBallot(
    val countPollingStations: Int,
    val numElections: Int,
    val structure: List<BcbBallotStructure>,
    val countVoters: Long
): TransactionData {
}