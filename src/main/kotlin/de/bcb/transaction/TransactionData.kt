package de.bcb.transaction

interface TransactionData {
    fun toDataString(): String

    // TODO ugly, so the object itself create the list for sending and another class has t parse it -> logic twice
    fun toDataList(): List<String>
}