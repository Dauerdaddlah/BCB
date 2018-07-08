package de.bcb.block

import de.bcb.BcbTimestamp
import de.bcb.BcbVersion
import de.bcb.security.BcbHash

class BcbBlock (
    var index: Int,
    var prevHash: BcbHash,
    var merkleHash: BcbHash,
    // proof-of-stake
    // erstellt über sha 256 der belegt, das er Zuständig war
    var hash: BcbHash,
    // wann wurde der de.bcb.Block erstellt
    var timestamp: BcbTimestamp,
    // welche de.bcb.Version wird verwendet
    var version: BcbVersion,
    // öffentlich - von jedem einsehbar
    var data: BcbBlockData
)