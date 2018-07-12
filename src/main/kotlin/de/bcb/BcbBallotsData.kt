package de.bcb

import de.bcb.ballot.BcbBallots
import de.bcb.ballot.BcbBallotsStructure

class BcbBallotsData {
    val pollingStations = mutableListOf<String>()
    private val _voters = mutableMapOf<String, Voter>()
    val voters = _voters.values
    lateinit var structure: BcbBallotsStructure

    fun addVoter(encrypedName: String, station: String) {
        _voters[encrypedName] = Voter(encrypedName, station)
    }

    fun ballotsGiven(ballots: BcbBallots, station: String) {

    }

    fun voterUsed(encryptedName: String, station: String) {

    }

    data class Voter(
        val encryptedName: String,
        val station: String
    ) {
        var stationUsed: String? = null
    }
}