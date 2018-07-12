package de.bcb.conn

class BcbMsgs {
    companion object {
        val MSG_NAME = "Name"
        val MSG_NODE = "Node"
        val MSG_REQ_SIG_KEY = "RequestPublicSignatureKey"
        val MSG_REQ_ENC_KEY = "RequestPublicEncryptionKey"
        val MSG_REQ_SIG = "RequestSignature"

        val TRX_BALLOT = "Ballot"
        val TRX_BALLOT_STRUCT = "BallotStructure"
        val TRX_POLL_STAT = "PollingStation"
        val TRX_ROOT = "BcbShowRoot"
        val TRX_VOTER = "showVoter"
        val TRX_VOTER_USED = "voterUsed"
        val TRX_START_BALLOT = "startBallot"
        val TRX_END_BALLOT = "endBallot"
    }
}