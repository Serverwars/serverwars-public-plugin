package net.serverwars.sunsetPlugin.domain.tournamentparticipant.exceptions

class TournamentParticipantEnterException(val key: String, vararg val args: Any) : Exception(key)
