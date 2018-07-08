package de.bcb.conn

import java.io.Closeable
import java.net.InetAddress

interface BcbConnection : Closeable {
    val name: String
    val inetAddress: InetAddress
    fun sendMsg(msg: BcbMsg)
    fun onMsgReceive(action: (BcbMsg) -> Unit)
    fun onNameChanged(action: (oldName: String, newName: String, conn: BcbConnection) -> Unit)
}