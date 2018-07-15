package de.bcb

enum class BcbPhase {
    /**
     * phase in which the genesis block will be created.
     * this phase ends as soon as the genesis block is created
     */
    GENESIS,
    /**
     * phase after creation of the genesis block.
     * in this phase the locals, ballots and ids will be announced
     * this phase end as soon as everything is announced, as indicated
     * by the startBallot-transaction
     *
     * root
     */
    SHOW_BALLOT,
    /**
     * The ballot itself.
     * this phase starts with the startBallot-transaction and ends with the endBallotTransaction
     */
    BALLOT,
    /**
     * final phase, starting with the endBallot-transaction.
     * in this phase no valid transaction can be send anymore
     *
     * root
     */
    BALLOT_FINISHED

}