package de.bcb.ballot

class Ballot(
    val votes: List<String>,
    val valid: Boolean = !votes.isEmpty()
)