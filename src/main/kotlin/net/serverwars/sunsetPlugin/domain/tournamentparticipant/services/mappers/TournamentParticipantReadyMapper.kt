package net.serverwars.sunsetPlugin.domain.tournamentparticipant.services.mappers

import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryCreateMapper
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.models.tournamentparticipantready.TournamentParticipantReady
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.models.tournamentparticipantready.TournamentParticipantReadyDto

object TournamentParticipantReadyMapper {

    fun toDto(tournamentParticipantReady: TournamentParticipantReady): TournamentParticipantReadyDto {
        return TournamentParticipantReadyDto(
            queueEntryCreateDto = QueueEntryCreateMapper.toDto(tournamentParticipantReady.queueEntryCreate),

        )
    }
}