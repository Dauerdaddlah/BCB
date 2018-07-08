package de.bcb.conn

import de.bcb.launch
import de.bcb.user
import java.io.Closeable
import java.net.ServerSocket
import java.net.Socket
import java.util.*

class BcbNetworkImpl(
    port: Int
): BcbNetworkBase() {

    private val server = ServerSocket(port)

    override val port = port

    override var running: Boolean = false
        private set

    override fun start() {
        if(running) {
            return
        }

        launch { listen() }
    }

    private fun listen() {
        running = true

        while(!server.isClosed) {
            val socket = server.accept()

            val conn = BcbSocketConnection(socket, socket.inetAddress.toString())

            newConnection(conn)
        }

        running = false
    }

    override fun close() {
        server.close()
    }

    override fun connectToImpl(host: String, port: Int, name: String) {
        val socket = Socket(host,  port)
        val conn = BcbSocketConnection(socket, name)

        newConnection(conn)
    }
}