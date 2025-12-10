package net.serverwars.sunsetPlugin.domain.match.models.matchstatus

enum class MatchStatus(val value: String) {
    PREPARING("preparing"),
    OPEN("open"),
    ACTIVE("active"),
    FINISHED("finished"),
    ERROR("error");

    companion object {
        fun fromValue(value: String?): MatchStatus? {
            return entries.find { it.value == value }
        }
    }
}