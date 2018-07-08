package de.bcb.conn

import de.bcb.user

data class BcbMsg(
    val type: String,
    val data: List<String>,
    val sender: String
) {
    constructor(type: String, vararg data: String): this(type, data.asList(), user.name)
}