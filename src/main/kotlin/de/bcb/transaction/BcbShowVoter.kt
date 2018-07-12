package de.bcb.transaction

class BcbShowVoter(
        val pollingStationName: String,
        val encryptedVoter: String
): BcbTransactionDataBase("BcbShowVoter", pollingStationName, encryptedVoter) {
}