package de.bcb

import de.bcb.ballot.BcbBallots
import de.bcb.ballot.BcbBallotsStructure

class BcbBallotsData {
    private val _pollingStations = mutableMapOf<String, Station>()
    val pollingStations: Set<Station>
        get() { return _pollingStations.values.toSet() }

    private val _voters = mutableMapOf<String, Voter>()
    val voters: Set<Voter>
        get() { return _voters.values.toSet() }

    var structure: BcbBallotsStructure? = null

    private val _ballots = mutableListOf<BcbBallots>()
    val ballots: List<BcbBallots>
        get() { return _ballots }

    fun addStation(name: String) {
        _pollingStations[name] = Station(name)
    }

    fun addVoter(encrypedName: String, station: String) {
        _voters[encrypedName] = Voter(encrypedName, station)
        _pollingStations[station]!!.voters++
    }

    fun ballotsGiven(ballots: BcbBallots, station: String) {
        _pollingStations[station]!!.votersOpen--
        _ballots += ballots
    }

    fun voterUsed(encryptedName: String, station: String, voted: Boolean) {
        if (voted) {
            _pollingStations[station]!!.votersOpen++
        }
        _voters[encryptedName]!!.stationUsed = station
        _voters[encryptedName]!!.voted = voted
    }

    fun station(name: String) : Station? {
        return _pollingStations[name]
    }

    fun voter(encryptedName: String) : Voter? {
        return _voters[encryptedName]
    }

    data class Voter(
        val encryptedName: String,
        val station: String
    ) {
        /** name of the station where the voter voted */
        var stationUsed: String? = null
        /** whether the voter actually voted (Wähler - Nichtwähler) */
        var voted: Boolean = false
    }

    data class Station(
        val name: String
    ) {
        /** number of voters assigned to this station */
        var voters = 0
        /**
         * number of voters enabled for voting at this station.
         * This value is initially 0 and increases for each given voter
         * and decreases for each given ballots
         *
         */
        var votersOpen = 0
    }
}