package net.serverwars.sunsetPlugin.domain.match.services

import java.util.UUID

object MatchService {

    private var matchUuid: UUID? = null

    suspend fun checkInMatch(): Boolean {
        val matchList = MatchDataAccess.getActiveMatchList()
        return (matchList.pagination.totalNumberOfElements ?: 0L) > 0L
    }

    fun setMatch(matchUuid: UUID?) {
        this.matchUuid = matchUuid
    }

    fun hasMatch(): Boolean = this.matchUuid != null
}