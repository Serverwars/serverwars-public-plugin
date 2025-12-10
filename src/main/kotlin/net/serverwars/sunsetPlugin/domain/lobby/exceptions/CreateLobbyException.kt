package net.serverwars.sunsetPlugin.domain.lobby.exceptions

class CreateLobbyException(val key: String, vararg val args: Any) : Exception(key)