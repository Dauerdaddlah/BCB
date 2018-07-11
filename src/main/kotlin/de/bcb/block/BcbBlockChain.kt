package de.bcb.block

class BcbBlockChain(
) {
    private val blocks = mutableListOf<BcbBlock>()
    val empty: Boolean
        get() = blocks.isEmpty()

    fun addBlock(block: BcbBlock) {
        blocks += block
    }

    operator fun plusAssign(block: BcbBlock) = addBlock(block)
}