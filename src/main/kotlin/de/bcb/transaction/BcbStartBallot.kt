package de.bcb.transaction

class BcbStartBallot(
    val countPollingStations: Int,
    val numElections: Int,
    val countVoters: Long
): BcbTransactionDataBase("BcbStartBallot", countPollingStations, numElections, countVoters) {
}