package de.bcb.conn

import de.bcb.launch
import java.io.*
import java.net.InetAddress
import java.net.Socket
import java.util.concurrent.LinkedBlockingDeque

class BcbSocketConnection(
        socket: Socket,
        name: String
) : BcbConnectionBase(name) {
    private val socket = socket
    override val inetAddress: InetAddress = socket.inetAddress

    private val writer: Writer
    private val reader: Reader

    init {
        writer = Writer(socket.getOutputStream())
        reader = Reader(socket.getInputStream())

        writer.start()
        reader.start()
    }

    override fun sendMsg(msg: BcbMsg) {
        writer.queue.addLast(msg)
    }

    override fun close() {
        writer.close()
        reader.close()
        socket.close()
    }

    inner class Writer(
            out: OutputStream
    ): Closeable {
        private val out = BufferedWriter(out.writer(charset))
        val queue = LinkedBlockingDeque<BcbMsg>()

        fun start() {
            launch { run() }
        }

        private fun run() {
            while(!socket.isClosed) {
                out.use {
                    val msg = queue.removeFirst()

                    out.write(msg.toMsgString())
                }
            }
        }

        override fun close() {
            out.close()
        }
    }

    inner class Reader(
            inp: InputStream
    ): Closeable {
        private val inp = BufferedReader(inp.reader(charset))

        fun start() {
            launch { run() }
        }

        private fun run() {
            while(!socket.isClosed) {
                val buffer = CharArray(1024)
                inp.use {
                    val length = inp.read(buffer)

                    dataReceived(String(buffer, 0, length))
                }
            }
        }

        override fun close() {
            inp.close()
        }
    }
}