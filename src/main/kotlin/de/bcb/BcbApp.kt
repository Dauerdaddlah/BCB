package de.bcb

import de.bcb.conn.BcbNetwork
import de.bcb.conn.BcbNetworkImpl

fun main(args: Array<String>) {
    val name = args[0]

    initUser(name)
    initNetwork(78965)
    network.start()

    val rootName = "root"
    val lokalNames = listOf("LokalA", "LokalB", "LokalC")

    when(name) {
        rootName -> {

        }
        in lokalNames -> {

        }
        else -> {

        }
    }
}

private fun initUser(name: String) {
    if(::_user.isInitialized) {
        throw IllegalStateException("User is already initialized")
    }

    _user = BcbUser(name)
}

fun initNetwork(port: Int) {
    if(::_network.isInitialized) {
        throw IllegalStateException("Network is already initialized")
    }

    _network = BcbNetworkImpl(port)
}

private lateinit var _user: BcbUser
val user: BcbUser
    get() = _user

private lateinit var _network: BcbNetwork
val network: BcbNetwork
    get() = _network