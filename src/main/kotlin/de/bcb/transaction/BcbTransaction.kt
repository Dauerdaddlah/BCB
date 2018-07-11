package de.bcb.transaction

import de.bcb.security.BcbSignature

class BcbTransaction(
    val data: TransactionData,
    val signatures: List<BcbSignature>
) {
    constructor(data: TransactionData, vararg signatures: BcbSignature) : this(data, signatures.asList())

    fun toDataString(): String {
        return "${data.toDataString()}|${signatures.joinToString("|")}"
    }
}