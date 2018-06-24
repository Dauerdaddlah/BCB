package de.bcb

import de.bcb.conn.BcbNetwork

fun main(args: Array<String>) {
    val name = args[0]

    val network = BcbNetwork(name)
    network.start()

    when(name) {
        "root" -> {

        }
        else -> {

        }
    }
}