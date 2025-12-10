package net.serverwars.sunsetPlugin.domain.queue.exceptions

class LeaveQueueException(val key: String, vararg val args: Any) : Exception(key)
