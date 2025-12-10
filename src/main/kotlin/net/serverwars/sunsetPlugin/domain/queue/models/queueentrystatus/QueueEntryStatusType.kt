package net.serverwars.sunsetPlugin.domain.queue.models.queueentrystatus

enum class QueueEntryStatusType(val value: String) {
    MATCH_FOUND("match_found"),
    NO_MATCH_FOUND("no_match_found"),
    LEFT_QUEUE("error_not_in_queue"),
    ERROR("error"),
    ;

    companion object {
        fun fromValue(value: String?): QueueEntryStatusType? {
            return QueueEntryStatusType.entries.find { it.value == value }
        }
    }
}