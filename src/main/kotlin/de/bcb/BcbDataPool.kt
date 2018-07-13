package de.bcb

import de.bcb.ballot.BcbBallotsStructure
import de.bcb.block.BcbBlock
import de.bcb.block.BcbBlockChain
import de.bcb.block.BcbBlockData
import de.bcb.security.BcbHash
import de.bcb.transaction.*

class BcbDataPool {
    var minNumTrxPerBlock: Int = 1
    var avgNumTrxPerBlock: Double = 10.toDouble()
    var maxNumTrxPerBlock: Int = Integer.MAX_VALUE

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
                    ballots.addStation(transaction.data.name)
                }
                is BcbShowBallotStructure -> ballots.structure = transaction.data.structure
                is BcbShowVoter -> ballots.addVoter(transaction.data.encryptedVoter, transaction.data.pollingStationName)

                is BcbStartBallot -> phase = BcbPhase.BALLOT

                is BcbBallotsGiven -> ballots.ballotsGiven(transaction.data.ballots, transaction.data.pollingStationName)
                is BcbVoterUsed -> ballots.voterUsed(transaction.data.encryptedVoter, transaction.data.pollingStationName, transaction.data.voted)

                is BcbEndBallot -> phase = BcbPhase.BALLOT_FINISHED
            }

            if (// first block needs to be created directly
                (chain.empty && phase != BcbPhase.GENESIS) ||
                // last block needs to be created directly
                phase == BcbPhase.BALLOT_FINISHED ||
                // max number of trx reached
                transactions.size >= maxNumTrxPerBlock ||
                // random creation of blocks for roughly all avgNumTrxPerBlock transactions, but they need to have a minimum of trx
                (transactions.size >= minNumTrxPerBlock && Math.random() < (1 / avgNumTrxPerBlock))) {
                mine()
            }
        } else {
            TODO()
        }
    }

    private fun checkTransaction(t: BcbTransaction): Boolean {
        when(phase) {
            BcbPhase.GENESIS -> {
                if (t.data !is BcbShowRoot ||
                    !t.checkSign(0, nameRoot)) {
                    return false
                }
            }
            BcbPhase.SHOW_BALLOT -> {
                when (t.data) {
                    is BcbShowVoter -> {
                        if (ballots.voter(t.data.encryptedVoter) != null ||
                            !t.checkSign(0, nameRoot) ||
                            !t.checkSign(1, t.data.pollingStationName)) {
                            return false
                        }
                    }
                    is BcbShowPollingStation -> {
                        if (ballots.station(t.data.name) != null ||
                            !t.checkSign(0, nameRoot) ||
                            !t.checkSign(1, t.data.name)) {
                            return false
                        }
                    }
                    is BcbShowBallotStructure -> {
                        if (ballots.structure != null ||
                            !t.checkSign(0, nameRoot)) {
                            return false
                        }
                    }
                    is BcbStartBallot -> {
                        if (!t.checkSign(0, nameRoot) ||
                            ballots.pollingStations.isEmpty() ||
                            ballots.voters.isEmpty() ||
                            ballots.structure?.structures?.size?:0 == 0 ||
                            t.data.countPollingStations != ballots.pollingStations.size ||
                            t.data.countVoters != ballots.voters.size.toLong() ||
                            t.data.numElections != ballots.structure?.structures?.size?:0
                        ) {
                            return false
                        }
                    }
                    else -> return false
                }
            }
            BcbPhase.BALLOT -> {
                when (t.data) {
                    is BcbBallotsGiven -> {
                        if(!t.checkSign(0, t.data.pollingStationName) ||
                            ballots.station(t.data.pollingStationName)?.votersOpen?:0 == 0) {
                            return false
                        }
                    }
                    is BcbVoterUsed -> {
                        if (!t.checkSign(0, nameRoot) ||
                            !t.checkSign(1, t.data.pollingStationName) ||
                            // voter already used
                            ballots.voter(t.data.encryptedVoter)?.stationUsed != null ||
                            // station does not exist
                            ballots.station(t.data.pollingStationName) == null ||
                            // if the voter did not vote (nichtwähler) only the original station can announce that
                            (!t.data.voted && ballots.voter(t.data.encryptedVoter)?.station != t.data.pollingStationName)) {
                            return false
                        }
                    }
                    is BcbEndBallot -> {
                        if (ballots.voters.find { it.stationUsed == null } != null ||
                            ballots.pollingStations.find { it.votersOpen > 0 } != null ||
                            !t.checkSign(0, nameRoot)) {
                            return false
                        }
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

    private fun BcbTransaction.checkSign(indexSign: Int, signer: String): Boolean {
        return user(signer).signature!!.verifySignature(
            data = data.toDataString(),
            signature = signatures[indexSign]
        )
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