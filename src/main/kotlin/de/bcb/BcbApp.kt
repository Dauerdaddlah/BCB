package de.bcb

import com.fasterxml.jackson.databind.ObjectMapper
import de.bcb.ballot.Ballot
import de.bcb.ballot.BcbBallotStructure
import de.bcb.ballot.BcbBallots
import de.bcb.ballot.BcbBallotsStructure
import de.bcb.security.BcbHasher
import de.bcb.security.BcbSha2Hasher
import de.bcb.transaction.*
import io.javalin.ApiBuilder.get
import io.javalin.ApiBuilder.post
import io.javalin.Javalin

// to avoid compile errors
val user = BcbUser("user")

fun main(args: Array<String>) {
    //if(args.isEmpty()) {
    //    throw Exception("Name of the local needs to be passed to the program")
    //}
    //val name = args[0]

    initUsers()
    initVoters()
    initBallotStructure()

    env.pool.minNumTrxPerBlock = 1
    env.pool.avgNumTrxPerBlock = 25.0
    env.pool.maxNumTrxPerBlock = 50

    creGenesisBlock()
    println("Genesis Block created ${env.pool.chain}")
    prepareBallots()
    println("Ballots prepared ${env.pool.chain}")
    startBallots()
    println("Ballots started ${env.pool.chain}")

    val app =
            Javalin
                .create()
                .enableCorsForAllOrigins()
                .port(8080)
                .start()

    app.routes {
        get("pollingStations") { ctx ->
            ctx.json(env.pollingStations)
        }

        get("ballots") { ctx ->
            ctx.json(env.structure)
        }

        get("blocks") { ctx ->
            ctx.json(env.pool.chain.blocks)
        }
        post("vote") { ctx ->
            // insert vote from frontend
            val mapper = ObjectMapper()
            val response = mapper.readValue(ctx.body(), ResponseData::class.java)

            // val response = ctx.bodyAsClass(ResponseData::class.java)
            val voter = BcbVoter(response.selectedPollingStation, response.constituencyId, response.firstName)
            val station = user(response.selectedPollingStation)

            useVoter(station.encryption!!.encrypt(voter.toDataString()), true, nameOnline)

            val data = BcbBallotsGiven(
                    pollingStationName = station.name,
                    ballots = BcbBallots(
                            listOf(
                                    Ballot(
                                            listOf(response.wahlkreisabgeordneter)),
                                    Ballot(
                                            listOf(response.partei))
                            )
                    )
            )

            env.pool.addTransaction(
                    BcbTransaction(
                            data,
                            user(nameOnline).signature!!.sign(data.toDataString())
                    )
            )

            fakeMissingBallots(60, 100, 0.9, 0.9)
            println("Ballots faked ${env.pool.chain}")
            endBallots()
            println("End Block created ${env.pool.chain}")
            printResults()
        }
    }
}

private fun initUsers() {
    env.users.add(BcbUser(nameRoot))
    env.pollingStationsSpecial += nameOnline
    env.pollingStationsSpecial += nameLetter
    env.pollingStations += "LokalA"
    env.pollingStations += "LokalB"
    env.pollingStations += "LokalC"
    env.pollingStationsAll += env.pollingStations
    env.pollingStationsAll += env.pollingStationsSpecial

    env.pollingStations.forEach { env.users.add(BcbUser(it)) }
    env.pollingStationsSpecial.forEach { env.users.add(BcbUser(it)) }

    rootUser().tryLoadFromFiles()
    env.pollingStations.map { user(it) }.forEach { it.tryLoadFromFiles() }
    env.pollingStationsSpecial.map { user(it) }.forEach { it.tryLoadFromFiles() }
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
            "Wahlkreisabgeordneter",
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
                "Partei",
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
    val data = BcbShowRoot(
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
    for (station in env.pollingStationsAll) {
        val data = BcbShowPollingStation(station)

        env.pool.addTransaction(
            BcbTransaction(
                data,
                rootUser().signature!!.sign(data.toDataString()),
                user(station).signature!!.sign(data.toDataString())
            )
        )
    }
    println("Prepared stations ${env.pool.chain}")
    for(voter in env.voters) {
        val station = user(voter.pollingStation)
        val data = BcbShowVoter(
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
    println("Prepared ${env.voters.size} voters ${env.pool.chain}")

    val data = BcbShowBallotStructure(env.structure)

    env.pool.addTransaction(
        BcbTransaction(
            data,
            rootUser().signature!!.sign(data.toDataString())
        )
    )
    println("Prepared structure ${env.pool.chain}")
}

fun startBallots() {
    val data = BcbStartBallot(
        countPollingStations = env.pollingStationsAll.size,
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

fun fakeMissingBallots(minChunkSize: Int, maxChunkSize: Int, turnout: Double, valid: Double) {
    val openVoters = env.pool.ballots.voters.filter { it.stationUsed == null }.toMutableList()

    while(!openVoters.isEmpty()) {
        val size = Math.min(openVoters.size, (minChunkSize + Math.random() * (maxChunkSize - minChunkSize)).toInt())

        val temp = mutableListOf<BcbBallotsData.Voter>()

        repeat(size) {
            val t = openVoters.randomElement()
            val voted = Math.random() < turnout
            val station = env.pollingStationsAll.randomElement()

            openVoters -= t
            if (voted) {
                temp += t
            }

            useVoter(
                encryptedName = t.encryptedName,
                station = if (voted) station else t.station,
                voted = voted
            )
        }

        for(voter in temp) {
            fakeBallotsFor(voter, false, valid)
        }

        println("Faked chunk of $size voters from which ${temp.size} actually voted ${env.pool.chain}")
    }
}

fun fakeBallotsFor(voter: BcbBallotsData.Voter, useVoter: Boolean = true, valid: Double) {
    if (voter.stationUsed == null && !useVoter) {
        TODO()
    }

    if (voter.stationUsed == null) {
        useVoter(
            encryptedName = voter.encryptedName,
            voted = true,
            station = env.pollingStationsAll.randomElement()
        )
    }

    val data = BcbBallotsGiven(
        pollingStationName = voter.stationUsed!!,
        ballots = fakeBallot(valid)
    )

    env.pool.addTransaction(
        BcbTransaction(
            data,
            user(voter.stationUsed!!).signature!!.sign(data.toDataString())
        )
    )
}

fun useVoter(encryptedName: String, voted: Boolean, station: String) {
    val data = BcbVoterUsed(
        encryptedVoter = encryptedName,
        pollingStationName = station,
        voted = voted
    )
    env.pool.addTransaction(
        BcbTransaction(
            data,
            rootUser().signature!!.sign(data.toDataString()),
            user(station).signature!!.sign(data.toDataString())
        )
    )
}

fun fakeBallot(valid: Double): BcbBallots {
    val ballots = mutableListOf<Ballot>()

    for (struct in env.pool.ballots.structure!!.structures) {
        if (Math.random() < valid) {
            val candidates = ArrayList(struct.candidates)
            val votes = mutableListOf<String>()

            repeat(struct.numVotes) {
                val c = candidates.randomElement()
                candidates -= c
                votes += c
            }

            ballots += Ballot(votes)
        } else {
            ballots += Ballot(emptyList(), false)
        }
    }

    return BcbBallots(ballots)
}

fun endBallots() {
    val data = BcbEndBallot(
    )

    env.pool.addTransaction(
        BcbTransaction(
            data,
            rootUser().signature!!.sign(data.toDataString())
        )
    )
}

fun printResults() {
    val voters = env.pool.ballots.voters.count { it.voted }
    val votersOnline = env.pool.ballots.voters.count { it.stationUsed == nameOnline }
    val votersLetter = env.pool.ballots.voters.count { it.stationUsed == nameLetter }
    val votersOutside = env.pool.ballots.voters.count { it.stationUsed != it.station } - votersOnline - votersLetter

    val ballots = env.pool.ballots.ballots.size
    val ballotsInvalid = env.pool.ballots.ballots.count {b -> b.ballots.find { it.valid } == null }
    val ballotsPartiallyInvalid = env.pool.ballots.ballots.count {b -> b.ballots.count {bb -> bb.valid }.let { it > 0 && it < b.ballots.size } }

    println("votes: $voters")
    println("nonvoters ${env.voters.size - voters}")
    println("${voters / env.voters.size.toDouble() * 100.0} %")
    println()
    println("voting at some other place: $votersOutside")
    println("online voters: $votersOnline")
    println("voters using letters $votersLetter")
    println()
    println("Ballots given: $ballots")
    println("Invalid ballots $ballotsInvalid")
    println("Partially invalid ballots $ballotsPartiallyInvalid")
}

fun user(name: String): BcbUser {
    return env.users.find { it.name == name }!!
}

inline fun rootUser(): BcbUser { return user(nameRoot) }

object env {
    val users = mutableListOf<BcbUser>()

    val pollingStationsAll = mutableListOf<String>()
    val pollingStations = mutableListOf<String>()
    val pollingStationsSpecial = mutableListOf<String>()

    val pool = BcbDataPool()

    val hasher: BcbHasher = BcbSha2Hasher()

    val voters = mutableListOf<BcbVoter>()

    var structure: BcbBallotsStructure = BcbBallotsStructure(emptyList())
}

fun <T> List<T>.randomElement(): T {
    return this[Math.min(size, (Math.random() * size).toInt())]!!
}