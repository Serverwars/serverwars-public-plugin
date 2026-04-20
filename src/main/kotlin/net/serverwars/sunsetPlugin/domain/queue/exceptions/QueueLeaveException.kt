package net.serverwars.sunsetPlugin.domain.queue.exceptions

class QueueLeaveException(val key: String, vararg val args: Any) : Exception(key)
