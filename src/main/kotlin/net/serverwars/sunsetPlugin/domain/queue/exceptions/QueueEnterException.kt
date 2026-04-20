package net.serverwars.sunsetPlugin.domain.queue.exceptions

class QueueEnterException(val key: String, vararg val args: Any) : Exception(key)
