package net.serverwars.sunsetPlugin.domain.tournamentparticipant.services.mappers

import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryCreateMapper
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.models.tournamentparticipantenter.TournamentParticipantEnter
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.models.tournamentparticipantenter.TournamentParticipantEnterDto

object TournamentParticipantEnterMapper {

    fun toDto(tournamentParticipantEnter: TournamentParticipantEnter): TournamentParticipantEnterDto {
        return TournamentParticipantEnterDto(
            queueEntryCreateDto = QueueEntryCreateMapper.toDto(tournamentParticipantEnter.queueEntryCreate),
        )
    }
}