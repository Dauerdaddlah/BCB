package de.bcb.conn

data class BcbMsg(
        val type: String,
        val data: List<String>
) {
}