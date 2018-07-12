package de.bcb.transaction

class BcbVoterUsed(
        val pollingStationName: String,
        val encryptedVoter: String,
        val voted: Boolean
): BcbTransactionDataBase("BcbVoterUsed", pollingStationName, encryptedVoter, voted) {
}