package net.serverwars.sunsetPlugin.domain.lobby.exceptions

class UpdateLobbyException(val key: String, vararg val args: Any) : Exception(key)