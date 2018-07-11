package de.bcb.mining

import de.bcb.launch

fun startMining() {
    if (isMining) {
        return
    }

    miner = BcbMiner()
    miner!!.startMining()
}

fun stopMining() {
    miner?.stopMining()
    miner = null
}

val isMining: Boolean
    get() {
        return miner != null && miner.isMining
    }

private var miner: BcbMiner? = null

private class BcbMiner {
    var isMining: Boolean = false
        private set

    fun startMining() {
        if (isMining) {
            return
        }

        isMining = true
        launch {
            loop()
        }
    }

    fun loop() {
        while(isMining) {

        }
        isMining = false
    }

    fun stopMining() {
        isMining = false
    }
}