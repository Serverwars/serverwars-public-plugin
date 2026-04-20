package net.serverwars.sunsetPlugin.domain.tournamentparticipant.models.tournamentparticipantenter

import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreate.QueueEntryCreate

data class TournamentParticipantEnter(
    val queueEntryCreate: QueueEntryCreate,
)
