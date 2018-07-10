package de.bcb

class BcbVoter(
    val pollingStation: String,
    val id: String,
    val name: String
) {
    fun toDataString(): String {
        return "$pollingStation|$id|$name"
    }
}