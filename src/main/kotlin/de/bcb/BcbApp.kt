package de.bcb

import de.bcb.conn.BcbNetwork
import de.bcb.conn.BcbNetworkImpl
import de.bcb.security.BcbHasher
import de.bcb.security.BcbSha2Hasher
import java.lang.Exception

fun main(args: Array<String>) {
    if(args.isEmpty()) {
        throw Exception("Name of the local needs to be passed to the program")
    }
    val name = args[0]

    initUser(name)
    initNetwork(78965)
    initDataPool()
    initHasher()

    network.start()

    TODO("Where to put network logic (how to react on each msg, handling incoming transactions, blocks, etc.)")

    when(name) {
        nameRoot -> {
            RootLogic().start()
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
    user.tryLoadFromFiles()
}

fun initNetwork(port: Int) {
    if(::_network.isInitialized) {
        throw IllegalStateException("Network is already initialized")
    }

    _network = BcbNetworkImpl(port)
}

fun initDataPool() {
    if(::_dataPool.isInitialized) {
        throw IllegalStateException("Datapool is already initialized")
    }

    _dataPool = BcbDataPool()
}

fun initHasher() {
    if (::_hasher.isInitialized) {
        throw IllegalStateException("Hasher is already initialized")
    }

    _hasher = BcbSha2Hasher()
}

private lateinit var _user: BcbUser
val user: BcbUser
    get() = _user

private lateinit var _network: BcbNetwork
val network: BcbNetwork
    get() = _network

private lateinit var _dataPool: BcbDataPool
val dataPool: BcbDataPool
    get() = _dataPool

private lateinit var _hasher: BcbHasher
val hasher: BcbHasher
    get() = _hasher
