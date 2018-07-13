package de.bcb.block

class BcbBlockChain(
) {
    private val blocks = mutableListOf<BcbBlock>()
    val empty: Boolean
        get() = blocks.isEmpty()
    val lastBlock: BcbBlock?
        get() {
            return blocks.lastOrNull()
        }
    val size: Int
        get() = blocks.size

    fun addBlock(block: BcbBlock) {
        blocks += block
    }

    operator fun plusAssign(block: BcbBlock) = addBlock(block)

    fun countTransactions(): Long {
        return blocks.map { it.transactions.size }.sum().toLong()
    }

    override fun toString(): String {
        return "Blockchain[blocks: $size, transactions: ${countTransactions()}]"
    }
}