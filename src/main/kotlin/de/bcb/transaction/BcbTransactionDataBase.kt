package de.bcb.transaction

open class BcbTransactionDataBase(type: String, vararg values: Any) : BcbTransactionData {
    private val values = listOf<Any>(type) + values.toList()

    override fun toDataString(): String {
        return values.joinToString("|")
    }
}