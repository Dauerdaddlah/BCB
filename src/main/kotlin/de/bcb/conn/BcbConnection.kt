package de.bcb.conn

import java.io.*
import java.lang.StringBuilder
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.LinkedBlockingDeque

class BcbConnection(
        socket: Socket
): Closeable {
    companion object {
        val charset = StandardCharsets.UTF_8

        val startMark = '<'
        val endMark = '>'
        val separator = '|'
        val mask = '\''
    }

    private val msgReceivedListener = mutableListOf<(BcbMsg) -> Unit>()
    private val socket = socket
    val inetAddress = socket.inetAddress
    private val writer: Writer
    private val reader: Reader

    init {
        writer = Writer(socket.getOutputStream())
        reader = Reader(socket.getInputStream())

        writer.start()
        reader.start()
    }

    fun sendMsg(msg: BcbMsg) {
        writer.queue.addLast(msg)
    }

    fun onMsgReceive(action: (BcbMsg) -> Unit) {
        msgReceivedListener += action
    }

    private fun msgReceived(msg: BcbMsg) {
        msgReceivedListener.forEach { it.invoke(msg) }
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
            val t = Thread( { run() })
            t.isDaemon = true
            t.start()
        }

        private fun run() {
            while(!socket.isClosed) {
                out.use {
                    val msg = queue.removeFirst()

                    val builder = StringJoiner("$separator", "$startMark", "$endMark")

                    builder.add(mask(msg.type))
                    msg.data.forEach { builder.add(mask(it)) }

                    out.write(builder.toString())
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
            val t = Thread( { run() })
            t.isDaemon = true
            t.start()
        }

        private fun run() {
            while(!socket.isClosed) {
                val builder = StringBuilder()
                val buffer = CharArray(1024)
                inp.use {
                    val length = inp.read(buffer)

                    builder.append(String(buffer, 0, length))

                    analyze(builder)
                }
            }
        }

        private fun analyze(builder: StringBuilder) {
            while(true) {
                val end = builder.indexOf(endMark, mask)

                if(end == -1) {
                    return
                }

                val start = builder.lastIndexOf(startMark, end, mask)

                if(start != -1) {
                    val msgString = builder.subSequence(start + 1, end)
                    analyze(unmask(msgString))
                }

                builder.replace(0, end + 1, "")
            }
        }

        private fun analyze(msgString: CharSequence) {
            val parts = msgString.split(separator, mask)
            if(parts.isEmpty()) {
                return
            }
            val msg = BcbMsg(
                    parts[0],
                    if(parts.size == 1) Collections.emptyList() else parts.subList(1, parts.size))

            msgReceived(msg)
        }

        override fun close() {
            inp.close()
        }
    }

    fun mask(msgString: CharSequence): CharSequence {
        val builder = StringBuilder()
        val characters = "$startMark$endMark$separator$mask"

        for(c in msgString) {
            if(characters.contains(c)) {
                builder.append(mask)
            }
            builder.append(c)
        }

        return builder
    }

    fun unmask(msgString: CharSequence): CharSequence {
        var m = false
        val builder = StringBuilder()
        for(c in msgString) {
            if(!m && c == mask) {
                m = true
                continue
            }
            m = false
            builder.append(c)
        }
        return builder
    }

    fun CharSequence.lastIndexOf(c: Char, end: Int = length, mask: Char): Int {
        var index = -1
        while(true) {
            val i = indexOf(c, index + 1, end, mask)
            if(i == -1) {
                break
            }
            index = i
        }
        return index
    }

    fun CharSequence.indexOf(c: Char, mask: Char): Int {
        return indexOf(c, 0,  mask)
    }

    fun CharSequence.indexOf(c: Char, start: Int = 0, mask: Char): Int {
        return indexOf(c, start, length, mask)
    }

    fun CharSequence.indexOf(c: Char, start: Int = 0, end: Int = length, mask: Char): Int {
        var m = false
        var s = Math.max(start, 0)
        var e = Math.min(end, length)
        s = Math.min(s, e)

        for (i in s..e) {
            val ch = get(i)

            if(m) {
                m = false
                continue
            }
            if(ch == c) {
                return i
            }
            if(ch == mask) {
                m = true
            }
        }
        return -1
    }
}