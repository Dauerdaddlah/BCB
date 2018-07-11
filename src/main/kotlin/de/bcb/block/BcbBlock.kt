package de.bcb.block

import de.bcb.BcbTimestamp
import de.bcb.BcbUser
import de.bcb.BcbVersion
import de.bcb.security.BcbHash
import de.bcb.security.BcbSignature
import de.bcb.transaction.BcbTransaction
import java.time.LocalDateTime

class BcbBlock (
    val index: Int,
    val prevHash: BcbHash,
    // proof-of-stake
    // erstellt über sha 256 der belegt, das er Zuständig war
    val hash: BcbHash,
    // wann wurde der de.bcb.Block erstellt
    val timestamp: BcbTimestamp,
    // welche de.bcb.Version wird verwendet
    val version: BcbVersion,
    val transactions: List<BcbTransaction>,

    val creator: String,
    val signature: BcbSignature
)