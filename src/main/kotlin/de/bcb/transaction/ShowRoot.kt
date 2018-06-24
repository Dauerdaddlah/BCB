package de.bcb.transaction

import de.bcb.security.BcbSignature
import java.security.PublicKey

class ShowRoot(
        val signatureKey: PublicKey,
        val signatureRoot: BcbSignature
): Transaction {

}