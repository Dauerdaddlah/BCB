package de.bcb

import de.bcb.ballot.BcbBallotStructure
import de.bcb.ballot.BcbBallotsStructure
import de.bcb.conn.BcbNetwork
import de.bcb.conn.BcbNetworkImpl
import de.bcb.security.BcbHasher
import de.bcb.security.BcbSha2Hasher
import de.bcb.transaction.*
import java.lang.Exception

fun main(args: Array<String>) {
    if(args.isEmpty()) {
        throw Exception("Name of the local needs to be passed to the program")
    }
    val name = args[0]

    initUsers()
    initVoters()
    initBallotStructure()
    creGenesisBlock()
    prepareBallots()
    startBallots()
}

private fun initUsers() {
    env.users.add(BcbUser(nameRoot))
    env.pollingStations += "LokalA"
    env.pollingStations += "LokalB"
    env.pollingStations += "LokalC"

    env.pollingStations.forEach { env.users.add(BcbUser(it)) }
    env.users.add(BcbUser("LokalA"))
    env.users.add(BcbUser("LokalB"))
    env.users.add(BcbUser("LokalC"))

    rootUser().tryLoadFromFiles()
    env.pollingStations.map { user(it) }.forEach { it.tryLoadFromFiles() }
}

private fun initVoters() {
    // station id name
    var c1 = 'a' - 1
    for (station in env.pollingStations) {
        c1++
        var count = 0
        for (c2 in 'a'..'z') {
            for (c3 in 'a'..'z') {
                count++
                env.voters += BcbVoter(
                    pollingStation = station,
                    id = count.toString(),
                    name = "$c1$c2$c3"
                )
            }
        }
    }
}

private fun initBallotStructure() {
    env.structure = BcbBallotsStructure(
        listOf(
            BcbBallotStructure(
            1,
                listOf(
                    "Ken Kannix",
                    "Willi Willnix",
                    "Michael machtnix",
                    "Lukas Lügtnur",
                    "Ulla Unvorsichtig",
                    "Zoe Zerstörtalles",
                    "Sarah Schläftimmer",
                    "Björn Blöd-von-und-zu-betrunken"
                )
            ),
            BcbBallotStructure(
                1,
                listOf(
                    "SPD",
                    "CDU",
                    "DIe Grünen"
                )
            )
        )
    )
}

private fun creGenesisBlock() {
    val data = ShowRoot(
        encryptionKey = rootUser().encryption!!.publicKey,
        signatureKey = rootUser().signature!!.publicKey
    )

    env.pool.addTransaction(
        BcbTransaction(
            data,
            rootUser().signature!!.sign(data.toDataString())
        )
    )

    env.pool.mine()
}

fun prepareBallots() {
    for (station in env.pollingStations) {
        val data = ShowPollingStation(station)

        env.pool.addTransaction(
            BcbTransaction(
                data,
                rootUser().signature!!.sign(data.toDataString()),
                user(station).signature!!.sign(data.toDataString())
            )
        )
    }
    for(voter in env.voters) {
        val station = user(voter.pollingStation)
        val data = ShowVoter(
            voter.pollingStation,
            station.encryption!!.encrypt(voter.toDataString()))

        env.pool.addTransaction(
            BcbTransaction(
                data,
                rootUser().signature!!.sign(data.toDataString()),
                station.signature!!.sign(data.toDataString())
            )
        )
    }

    val data = ShowBallotStructure(env.structure)

    env.pool.addTransaction(
        BcbTransaction(
            data,
            rootUser().signature!!.sign(data.toDataString())
        )
    )
}

fun startBallots() {
    val data = StartBallot(
        countPollingStations = env.pollingStations.size,
        countVoters = env.voters.size.toLong(),
        numElections = env.structure.structures.size
    )

    env.pool.addTransaction(
        BcbTransaction(
            data,
            rootUser().signature!!.sign(data.toDataString())
        )
    )
}

fun user(name: String): BcbUser {
    return env.users.find { it.name == name }!!
}

inline fun rootUser(): BcbUser = user(nameRoot)

object env {
    val users = mutableListOf<BcbUser>()

    val pollingStations = mutableListOf<String>()

    val pool = BcbDataPool()

    val hasher: BcbHasher = BcbSha2Hasher()

    val voters = mutableListOf<BcbVoter>()

    var structure: BcbBallotsStructure = BcbBallotsStructure(emptyList())
}