package net.serverwars.sunsetPlugin.domain.tournamentparticipant.models.tournamentparticipantready

import kotlinx.serialization.Serializable

@Serializable
data class TournamentParticipantReadyDto(
    val serverSecret: String,
)
