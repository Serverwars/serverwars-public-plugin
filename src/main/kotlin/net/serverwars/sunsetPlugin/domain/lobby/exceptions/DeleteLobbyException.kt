package net.serverwars.sunsetPlugin.domain.lobby.exceptions

class DeleteLobbyException(val key: String, vararg val args: Any) : Exception(key)