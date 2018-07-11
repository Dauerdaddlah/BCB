package de.bcb.block

import de.bcb.BcbUser
import de.bcb.BcbVersion
import de.bcb.env
import de.bcb.security.BcbHash
import de.bcb.security.BcbSignature
import de.bcb.transaction.BcbTransaction
import java.time.LocalDateTime

class BcbBlockData(
     var prevBlock: BcbBlock?,
     var version: BcbVersion,
     val transactions: MutableList<BcbTransaction> = mutableListOf()
 ) {
    fun createBlock(user: BcbUser): BcbBlock {

        val sign = user.signature!!.sign(toCheckString())

        return BcbBlock(
            version = version,
            prevHash = prevBlock?.hash ?: BcbHash(ByteArray(1)),
            index = (prevBlock?.index ?: -1) + 1,
            timestamp = LocalDateTime.now(),
            transactions = transactions,
            hash = creHash(user, sign),

            creator = user.name,
            signature = sign
        )
    }

    private fun creHash(user: BcbUser, sign: BcbSignature): BcbHash {
        return env.hasher.hashcode(toHashString(user, sign))
    }

    private fun toHashString(user: BcbUser, sign: BcbSignature): String {
        return "${toCheckString()}|${user.name}|$sign"
    }

    fun toCheckString(): String {
        return "$version|${prevBlock?.index?.plus(1)?:0}|${prevBlock?.hash?:0}${toString(transactions)}"
    }

    fun toString(ts: List<BcbTransaction>): String {
        return ts.map { it.toDataString() }.joinToString { "|" }
    }
}