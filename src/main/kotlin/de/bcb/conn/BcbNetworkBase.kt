package de.bcb.conn

import de.bcb.user
import java.util.*

abstract class BcbNetworkBase : BcbNetwork {

    private val conns: MutableMap<String, BcbConnectionBase> = Collections.synchronizedMap(mutableMapOf())
    private val connectListener = mutableListOf<(BcbConnection, BcbNetwork) -> Unit>()
    private val msgListener = mutableListOf<(BcbMsg, BcbNetwork) -> Unit>()

    override val connectionNames: Set<String>
        get() {
            return conns.keys
        }

    override fun sendToAll(msg: BcbMsg) {
        conns.values.forEach { it.sendMsg(msg) }
    }

    override fun connection(name: String): BcbConnection? {
        return conns[name]
    }

    protected fun newConnection(conn: BcbSocketConnection) {
        if(conns.containsKey(conn.name)) {
            // connection already present
            conn.close()
            return
        }

        synced {
            conns[conn.name] = conn
        }

        conn.onMsgReceive { receive(it) }

        connectListener.forEach { it.invoke(conn, this) }

        sendName(conn)
        sendNodes(conn)
    }

    private fun sendName(conn: BcbSocketConnection) {
        conn.sendMsg(
            BcbMsg(
                BcbMsgs.MSG_NAME,
                user.name
            )
        )
    }

    private fun sendNodes(conn: BcbSocketConnection) {
        conns.forEach {n, c ->
            if(c != conn) {
                conn.sendMsg(
                    BcbMsg(
                        BcbMsgs.MSG_NODE,
                        c.inetAddress.toString(),
                        n
                    )
                )
            }
        }
    }

    private fun receive(msg: BcbMsg) {
        when(msg.type) {
            BcbMsgs.MSG_NAME -> {
                val name = msg.data[0]

                if (msg.sender == name) {
                    return
                }

                if (connectionNames.contains(name)) {
                    TODO()
                }

                renameConn(msg.sender, name)
            }

            BcbMsgs.MSG_NODE -> {
                val host = msg.data[0]
                val name = if(msg.data.size == 1) host else msg.data[1]

                if(!conns.containsKey(name)) {
                    connectTo(host, port,  name)
                }
            }
        }

        msgListener.forEach { it.invoke(msg, this) }
    }

    override fun connectTo(host: String, port: Int, name: String) {
        if(connectionNames.contains(name)) {
            return
        }
        if(connectionNames.contains(host)) {
            renameConn(host, name)
            return
        }

        connectToImpl(host, port, name)
    }

    protected abstract fun connectToImpl(host: String, port: Int, name: String)

    override fun close() {
        synced {
            conns.clear()
        }
    }

    private inline fun <R> synced(block: () -> R): R {
        synchronized(this) {
            return block.invoke()
        }
    }

    private inline fun renameConn(nameOld: String, name: String) {
        synced {
            val conn = conns[nameOld]!!
            conns[name] = conn
            conns.remove(nameOld)
            conn.name = name
        }
    }

    override fun onConnect(action: (BcbConnection, BcbNetwork) -> Unit) {
        connectListener += action
    }

    override fun onMsgReceived(action: (BcbMsg, BcbNetwork) -> Unit) {
        msgListener += action
    }
}