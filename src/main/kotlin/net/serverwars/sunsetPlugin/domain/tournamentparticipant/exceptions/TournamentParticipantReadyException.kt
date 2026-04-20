package net.serverwars.sunsetPlugin.domain.tournamentparticipant.exceptions

class TournamentParticipantReadyException(val key: String, vararg val args: Any) : Exception(key)
