package de.bcb

import de.bcb.security.BcbHash
import java.time.LocalDateTime

class BcbBlock (
    var index: Int = 0,
    var prevHash: BcbHash = 0,
    var merkleHash: BcbHash = 0,
    // proof-of-stake
    // erstellt über sha 256 der belegt, das er Zuständig war
    var hash: BcbHash = 0,
    // wann wurde der de.bcb.Block erstellt
    var timestamp: BcbTimestamp = LocalDateTime.now(),
    // welche de.bcb.Version wird verwendet
    var version: BcbVersion = 100,
    // öffentlich - von jedem einsehbar
    var data: BcbBlockData = BcbBlockData()
)