package net.serverwars.sunsetPlugin.domain.match.models.matchlist

enum class MatchSortOption(val value: String) {
    RANDOM("random");

    companion object {
        fun fromValue(value: String?): MatchSortOption? {
            return entries.find { it.value == value }
        }
    }

}
