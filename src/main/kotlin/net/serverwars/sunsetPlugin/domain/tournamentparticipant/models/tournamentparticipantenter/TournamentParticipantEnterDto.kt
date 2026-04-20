package net.serverwars.sunsetPlugin.domain.tournamentparticipant.models.tournamentparticipantenter

import kotlinx.serialization.Serializable
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreate.QueueEntryCreateDto

@Serializable
data class TournamentParticipantEnterDto(
    val queueEntryCreateDto: QueueEntryCreateDto,
)
