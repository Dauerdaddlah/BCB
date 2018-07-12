package de.bcb.transaction

import de.bcb.security.BcbSignature

class BcbTransaction(
    val data: BcbTransactionData,
    val signatures: List<BcbSignature>
) {
    constructor(data: BcbTransactionData, vararg signatures: BcbSignature) : this(data, signatures.asList())

    fun toDataString(): String {
        return "${data.toDataString()}|${signatures.joinToString("|")}"
    }
}