package de.bcb.conn

import java.lang.StringBuilder
import java.nio.charset.StandardCharsets
import java.util.*

abstract class BcbConnectionBase(
    name: String
) : BcbConnection {

    private val msgReceivedListener = mutableListOf<(BcbMsg) -> Unit>()
    private val nameChangedListener = mutableListOf<(String, String, BcbConnection) -> Unit>()

    private val builder = StringBuilder()

    override var name: String = name
        set(value) {
            if(name != value) {
                val oldName = name
                field = value

                nameChangedListener.forEach { it.invoke(oldName, name, this) }
            }
        }

    override fun onNameChanged(action: (oldName: String, newName: String, conn: BcbConnection) -> Unit) {
        nameChangedListener += action
    }

    override fun onMsgReceive(action: (BcbMsg) -> Unit) {
        msgReceivedListener += action
    }

    protected fun msgReceived(msg: BcbMsg) {
        msgReceivedListener.forEach { it.invoke(msg) }
    }

    protected fun dataReceived(data: String) {
        builder.append(data)

        analyze(builder)
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
            if(parts.size == 1) Collections.emptyList() else parts.subList(1, parts.size),
            name)

        msgReceived(msg)
    }

    fun BcbMsg.toMsgString(): String {
        val builder = StringJoiner("$separator", "$startMark", "$endMark")

        builder.add(mask(type))
        data.forEach { builder.add(mask(it)) }

        return builder.toString()
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

    companion object {
        val charset = StandardCharsets.UTF_8

        val startMark = '<'
        val endMark = '>'
        val separator = '|'
        val mask = '\''

        protected fun mask(msgString: CharSequence): CharSequence {
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

        protected fun unmask(msgString: CharSequence): CharSequence {
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
    }
}