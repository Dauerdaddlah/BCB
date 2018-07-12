package de.bcb.transaction

import de.bcb.security.BcbPublicKey

data class BcbShowRoot(
    val signatureKey: BcbPublicKey,
    val encryptionKey: BcbPublicKey
): BcbTransactionDataBase("BcbShowRoot", signatureKey, encryptionKey)