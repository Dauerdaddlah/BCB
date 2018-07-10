package de.bcb.conn

import java.io.Closeable
import java.io.IOException

interface BcbNetwork : Closeable {
    val port : Int
    val running: Boolean
    val connectionNames: Set<String>

    @Throws(IOException::class)
    fun start()
    fun sendToAll(msg: BcbMsg)
    fun connection(name: String): BcbConnection?
    @Throws(IOException::class)
    fun connectTo(host: String, port: Int = this.port, name: String = host)

    fun onConnect(action: (BcbConnection, BcbNetwork) -> Unit)
    fun onMsgReceived(action: (BcbMsg, BcbNetwork) -> Unit)
}