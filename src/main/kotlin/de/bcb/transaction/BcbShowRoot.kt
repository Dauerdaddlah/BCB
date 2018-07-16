package de.bcb.transaction

import com.fasterxml.jackson.annotation.JsonIgnore
import de.bcb.security.BcbPublicKey

data class BcbShowRoot(
    @JsonIgnore
    val signatureKey: BcbPublicKey,
    @JsonIgnore
    val encryptionKey: BcbPublicKey
): BcbTransactionDataBase("BcbShowRoot", signatureKey, encryptionKey)