package net.serverwars.sunsetPlugin.domain.tournamentparticipant.models.tournamentparticipantready

import kotlinx.serialization.Serializable
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreate.QueueEntryCreateDto

@Serializable
data class TournamentParticipantReadyDto(
    val queueEntryCreateDto: QueueEntryCreateDto,

)
