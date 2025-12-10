package net.serverwars.sunsetPlugin.domain.match.services

object MatchService {

    suspend fun checkInMatch(): Boolean {
        val matchList = MatchDataAccess.getActiveMatchList()
        return (matchList.pagination.totalNumberOfElements ?: 0L) > 0L
    }
}