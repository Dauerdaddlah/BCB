package de.bcb.conn

import java.io.Closeable
import java.net.ServerSocket
import java.net.Socket
import java.util.*

class BcbNetwork(
        val name: String
): Closeable {
    companion object {
        val port = 78965
    }

    private val server: ServerSocket
    private val conns = Collections.synchronizedMap(mutableMapOf<String, BcbConnection>())
    private val connNames = Collections.synchronizedMap(mutableMapOf<BcbConnection, String>())

    init {
        server = ServerSocket(port)
    }

    fun start() {
        val t = Thread( { listen() } )
        t.start()
    }

    override fun close() {
        server.close()
        synchronized(this) {
            conns.clear()
            connNames.clear()
        }
    }

    private fun listen() {
        while(!server.isClosed) {
            val socket = server.accept()

            val conn = BcbConnection(socket)
            val name = socket.inetAddress.toString()

            newConnection(conn, name)
        }
    }

    private fun receive(conn: BcbConnection, msg: BcbMsg) {
        when(msg.type) {
            BcbMsgs.MSG_NAME -> {
                val nameOld = connNames[conn]!!
                val name = msg.data[0]
                synchronized(this) {
                    conns[name] = conn
                    connNames[conn] = name
                }
                conns.remove(nameOld)
            }

            BcbMsgs.MSG_NODE -> {
                val host = msg.data[0]
                val name = if(msg.data.size == 1) host else msg.data[1]

                if(!conns.containsKey(name)) {
                    connect(host, name)
                }
            }
        }
    }

    private fun connect(host: String, name: String = host) {
        val socket = Socket(host,  port)
        val conn = BcbConnection(socket)

        newConnection(conn, name)
    }

    private fun newConnection(conn: BcbConnection, name: String) {
        if(conns.containsKey(name)) {
            // connection already present
            conn.close()
            return
        }

        synchronized(this) {
            conns[name] = conn
            connNames[conn] = name
        }

        conn.onMsgReceive { receive(conn, it) }
        sendName(conn)
        sendNodes(conn)
    }

    private fun sendName(conn: BcbConnection) {
        conn.sendMsg(
                BcbMsg(
                        BcbMsgs.MSG_NAME,
                        listOf(
                                name
                        )
                )
        )
    }

    private fun sendNodes(conn: BcbConnection) {
        conns.forEach {n, c ->
            if(c != conn) {
                conn.sendMsg(
                        BcbMsg(
                                BcbMsgs.MSG_NODE,
                                listOf(
                                        c.inetAddress.toString(),
                                        n
                                )
                        )
                )
            }
        }
    }

    fun send(msg: BcbMsg) {
        conns.values.forEach {
            it.sendMsg(msg)
        }
    }
}