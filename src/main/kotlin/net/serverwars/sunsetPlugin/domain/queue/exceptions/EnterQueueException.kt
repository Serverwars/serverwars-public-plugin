package net.serverwars.sunsetPlugin.domain.queue.exceptions

class EnterQueueException(val key: String, vararg val args: Any) : Exception(key)
