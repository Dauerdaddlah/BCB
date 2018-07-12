package de.bcb

import de.bcb.ballot.BcbBallotsStructure
import de.bcb.block.BcbBlock
import de.bcb.block.BcbBlockChain
import de.bcb.block.BcbBlockData
import de.bcb.security.BcbHash
import de.bcb.transaction.*

class BcbDataPool {
    var avgNumTrxPerBlock: Double = 10.toDouble()

    val chain: BcbBlockChain = BcbBlockChain()

    private val _transactions = mutableListOf<BcbTransaction>()
    val transactions: List<BcbTransaction>
        get() { return _transactions }
    private val _miners = mutableListOf<BcbUser>()
    val miners: List<BcbUser> = _miners

    val ballots = BcbBallotsData()

    var phase = BcbPhase.GENESIS
        private set

    fun addTransaction(transaction: BcbTransaction) {
        if(checkTransaction(transaction)) {
            _transactions += transaction

            when(transaction.data) {
                is BcbShowRoot -> {
                    _miners += rootUser()
                    phase = BcbPhase.SHOW_BALLOT
                }

                is BcbShowPollingStation -> {
                    _miners += user(transaction.data.name)
                    ballots.pollingStations.add(transaction.data.name)
                }
                is BcbShowBallotStructure -> ballots.structure = transaction.data.structure
                is BcbShowVoter -> ballots.addVoter(transaction.data.encryptedVoter, transaction.data.pollingStationName)

                is BcbStartBallot -> phase = BcbPhase.BALLOT

                is BcbBallotsGiven -> ballots.ballotsGiven(transaction.data.ballots, transaction.data.pollingStationName)
                is BcbVoterUsed -> ballots.voterUsed(transaction.data.encryptedVoter, transaction.data.pollingStationName)

                is BcbEndBallot -> phase = BcbPhase.BALLOT_FINISHED
            }





            if (// first block needs to be created directly
                (chain.empty && phase != BcbPhase.GENESIS) ||
                // last block needs to be created directly
                phase == BcbPhase.BALLOT_FINISHED ||
                // random creation of blocks for roughly all avgNumTrxPerBlock transactions
                Math.random() < (1 / avgNumTrxPerBlock)) {
                mine()
            }
        }
    }

    private fun checkTransaction(t: BcbTransaction): Boolean {
        when(phase) {
            BcbPhase.GENESIS -> {
                if (t.data !is BcbShowRoot) {
                    return false
                }
            }
            BcbPhase.SHOW_BALLOT -> {
                when (t.data) {
                    is BcbShowVoter -> {
                        // check voter unique
                    }
                    is BcbShowPollingStation -> {
                        // check station unique
                    }
                    is BcbShowBallotStructure -> {
                        // check structure not already given
                    }
                    is BcbStartBallot -> {
                        // check parameters given
                    }
                    else -> return false
                }
            }
            BcbPhase.BALLOT -> {
                when (t.data) {
                    is BcbBallotsGiven -> {
                        // check polling station valid
                        // check polling station has already given one voter for this ballot
                    }
                    is BcbVoterUsed -> {
                        // check polling station valid
                        // check voter valid and not aleady used
                    }
                    is BcbEndBallot -> {
                        // check all voters already used and all ballots needed are given
                    }
                    else -> return false
                }
            }
            else -> {
                return false
            }
        }

        return true
    }

    fun addBlock(block: BcbBlock) {
        if(checkBlock(block)) {
            chain += block
        }
    }

    private fun checkBlock(block: BcbBlock): Boolean {
        if (chain.empty) {
            if(block.transactions.size != 1 ||
                    block.transactions[0].data !is BcbShowRoot) {
                return false
            }
        }

        val hash = env.hasher.hashcode(block.toCheckString())

        val miner = determineMiner(hash, ArrayList(miners))

        if (block.creator != miner.name) {
            TODO()
            return false
        }

        if (block.signature != miner.signature!!.sign(block.toCheckString())) {
            TODO()
            return false
        }

        if (block.hash != env.hasher.hashcode(block.toHashString())) {
            TODO()
            return false
        }

        return true
    }

    fun mine() {
        println("mining with ${transactions.size} transactions")
        if (transactions.isEmpty()) {
            // nothing to mine
            return
        }

        if(chain.empty) {
            // mine genesis block
            val t = _transactions.removeAt(0)

            val b = BcbBlockData(
                prevBlock = null,
                version = 100
            )

            b.transactions.add(t)

            addBlock(b.createBlock(rootUser()))
        } else {
            val b = BcbBlockData(
                prevBlock = chain.lastBlock!!,
                version = 100,
                transactions = ArrayList(transactions)
            )

            val hash = env.hasher.hashcode(b.toCheckString())

            val miner = determineMiner(hash, ArrayList(miners))

            addBlock(b.createBlock(miner))

            _transactions.removeAll(b.transactions)
        }
    }

    private fun determineMiner(hash: BcbHash, miners: List<BcbUser>): BcbUser {
        val minerList = ArrayList(miners)

        val powOf10 = Math.pow(10.toDouble(), (minerList.size / 10) + 1.toDouble()).toLong()
        val partPerMiner = powOf10 / minerList.size.toDouble()

        val hashAsLong = hash.toLong()

        val minerIndex = Math.min(minerList.size - 1, ((hashAsLong % powOf10) / partPerMiner).toInt())

        return miners[minerIndex]!!
    }
}