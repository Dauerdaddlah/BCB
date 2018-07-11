package de.bcb

import de.bcb.ballot.BcbBallotStructure
import de.bcb.conn.BcbMsg
import de.bcb.conn.BcbMsgs
import de.bcb.security.*
import de.bcb.transaction.*
import java.nio.file.Files
import java.nio.file.Paths

class RootLogic {
    fun start() {
        launch {
            val data = ShowRoot(
                encryptionKey = user.encryption!!.publicKey,
                signatureKey = user.signature!!.publicKey
            )

            dataPool.addTransaction(
                BcbTransaction(
                    data,
                    user.signature!!.sign(data.toDataString())
                )
            )

            getPollingStations().forEach { showPollingStation(it) }

            showBallotStructure(getBallotStructure())

            getAllVoters().forEach { showVoter(it) }

            startBallot(getPollingStations(), getBallotStructure(), getAllVoters())

            TODO("Wait and only function as miner until all voters voted + all ballots are sent")

            endBallot()
        }
    }

    private fun getPollingStations(): List<String> = listOf("LokalA", "LokalB", "LokalC")

    private fun showPollingStation(name: String) {
        while(name !in network.connectionNames) {
            // no connection to the polling station so wait a little bit
            Thread.sleep(1000)
        }

        val encKey = getPublicEncKey(name)
        val sigKey = getPublicSigKey(name)

        val data = ShowPollingStation(
            name,
            signatureKey = sigKey,
            encryptionKey = encKey
        )

        val signature = requestSignature(name, BcbMsgs.TRX_POLL_STAT, data)

        if (!verify(signature, sigKey, data)) {
            TODO("polling station did not sign the transaction for showing it")
        }

        addTransaction(
            BcbTransaction(
                data,
                user.signature!!.sign(data.toDataString()),
                signature
            )
        )
    }

    private fun getPublicEncKey(name: String): BcbPublicKey {
        // we could get the public key from any public source but for testing we just load it ourself
        return BcbPublicKey(Files.readAllBytes(Paths.get("keys", name, "encryption", "public.key")))
    }

    private fun getPublicSigKey(name: String): BcbPublicKey {
        // we could get the public key from any public source but for testing we just load it ourself
        return BcbPublicKey(Files.readAllBytes(Paths.get("keys", name, "signature", "public.key")))
    }

    private fun requestSignature(name: String, trx: String, data: TransactionData): BcbSignature {
        network.connection(name)!!.sendMsg(
            BcbMsg(
                BcbMsgs.MSG_REQ_SIG,
                listOf(trx) + data.toDataList()))

        // we could really ask for the signature but for testing we just create the signature ourself
        val signature = BcbSignatureSystem(
            privateKey = BcbPrivateKey(Files.readAllBytes(Paths.get("keys", name, "signature", "private.key"))),
            publicKey = BcbPublicKey(Files.readAllBytes(Paths.get("keys", name, "signature", "public.key")))
        )

        return signature.sign(data.toDataString())
    }

    private fun verify(signature: BcbSignature, key: BcbPublicKey, data: TransactionData): Boolean {
        val sig = BcbSignatureSystem(
            publicKey = key,
            privateKey = null
        )

        return sig.verifySignature(
            data = data.toDataString(),
            signature = signature
        )
    }

    private fun showBallotStructure(struct: List<BcbBallotStructure>) {
        val data = ShowBallotStructure(
            struct.size, struct
        )

        addTransaction(
            BcbTransaction(
                data,
                user.signature!!.sign(data.toDataString())
            )
        )
    }

    private fun getBallotStructure(): List<BcbBallotStructure> {
        TODO()
    }

    private fun showVoter(voter: BcbVoter) {
        val data = ShowVoter(voter.pollingStation, encrypt(voter))

        // we could also check that the polling station sign for the correct voter
        // if we let the station send the voter back encrypted using ower own public key
        // and control if it is the same as we sent to him
        val signature = requestSignature(voter.pollingStation, BcbMsgs.TRX_VOTER, data)

        addTransaction(
            BcbTransaction(
                data,
                user.signature!!.sign(data.toDataString()),
                signature
            )
        )
    }

    private fun encrypt(voter: BcbVoter): String {
        TODO("by now the polling station is in the datapool an so their should be a userobject of him anywhere, use that to get its public key")
        val encryption = BcbEncryptionSystem(publicKey = getPublicEncKey(voter.pollingStation), privateKey = null)

        return encryption.encrypt(voter.toDataString())
    }

    private fun getAllVoters(): List<BcbVoter> {
        return listOf(
            BcbVoter(
                "LokalA",
                "1",
                "Dieter"
            ),
            BcbVoter(
                "LokalA",
                "2",
                "Winfried"
            ),
            BcbVoter(
                "LokalA",
                "3",
                "Walter"
            ),
            BcbVoter(
                "LokalB",
                "1",
                "Wilhelmine"
            ),
            BcbVoter(
                "LokalB",
                "2",
                "Adelheid"
            ),
            BcbVoter(
                "LokalB",
                "3",
                "Brunhilde"
            ),
            BcbVoter(
                "LokalC",
                "1",
                "Kannix"
            ),
            BcbVoter(
                "LokalC",
                "2",
                "Willnix"
            ),
            BcbVoter(
                "LokalC",
                "3",
                "Machtnix"
            )
        )
    }

    private fun startBallot(
        stations: List<String>,
        struct: List<BcbBallotStructure>,
        voters: List<BcbVoter>
    ) {
        val data = StartBallot(
            countVoters = voters.size.toLong(),
            numElections = struct.size,
            structure = struct,
            countPollingStations = stations.size
        )
        addTransaction(
            BcbTransaction(
                data,
                user.signature!!.sign(data.toDataString())
            )
        )
    }

    private fun endBallot() {
        val data = EndBallot(

        )

        addTransaction(
            BcbTransaction(
                data,
                user.signature!!.sign(data.toDataString())
            )
        )
    }

    fun addTransaction(transaction: BcbTransaction) {
        TODO("I do currently not now where to put this shit")
    }
}