package net.serverwars.sunsetPlugin.domain.lobby.models

import net.kyori.adventure.audience.Audience
import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.domain.match.services.MatchDataAccess
import net.serverwars.sunsetPlugin.translations.sendTranslatedActionBarMessage
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import net.serverwars.sunsetPlugin.util.now
import net.serverwars.sunsetPlugin.util.playMatchFoundSound
import net.serverwars.sunsetPlugin.util.runSync
import org.bukkit.Bukkit
import java.util.*

data class Lobby(
    val lobbyUuid: UUID,
    val participantUuids: List<UUID>,
    val createdAtTimestamp: Long,
    val lobbySettings: LobbySettings,
    val invitations: List<LobbyInvitation>,
    val serverSlug: String,
) {
    companion object {
        val ALLOWED_LOBBY_SIZES = listOf(1, 3, 5, 10)

        fun create(lobbySettings: LobbySettings): Lobby {
            return Lobby(
                lobbyUuid = UUID.randomUUID(),
                participantUuids = emptyList(),
                createdAtTimestamp = now(),
                lobbySettings = lobbySettings,
                invitations = emptyList(),
                serverSlug = Config.getServerSlug(),
            )
        }
    }

    fun withLobbySettings(lobbySettings: LobbySettings): Lobby {
        return this.copy(lobbySettings = lobbySettings)
    }

    fun withPlayer(playerUuid: UUID): Lobby {
        return this.copy(participantUuids = this.participantUuids.plus(playerUuid))
    }

    fun withoutPlayer(playerUuid: UUID): Lobby {
        return this.copy(participantUuids = this.participantUuids.minus(playerUuid))
    }

    fun withInvite(invite: LobbyInvitation): Lobby {
        return this.copy(invitations = this.invitations.plus(invite))
    }

    fun withoutInvite(invite: LobbyInvitation): Lobby {
        return this.copy(invitations = this.invitations.minus(invite))
    }

    fun withoutInvites(): Lobby {
        return this.copy(invitations = emptyList())
    }

    fun sendMessage(translationKey: String, vararg args: Any) {
        getLobbyAudience().sendTranslatedMessage(translationKey, *args)
    }

    fun sendMessageExcluding(uuidToExclude: UUID, translationKey: String, vararg args: Any) {
        sendMessageExcluding(listOf(uuidToExclude), translationKey, *args)
    }

    fun sendMessageExcluding(uuidsToExclude: Collection<UUID>, translationKey: String, vararg args: Any) {
        getLobbyAudienceWithout(uuidsToExclude).sendTranslatedMessage(translationKey, *args)
    }

    fun sendActionBarMessage(translationKey: String, vararg args: Any) {
        getLobbyAudience().sendTranslatedActionBarMessage(translationKey, *args)
    }

    suspend fun matchFound(matchUuid: UUID) {
        sendActionBarMessage("queue.match_found.action_bar")
        sendMessage("queue.success.match_found")
        playMatchFoundSound(this)

        MatchDataAccess.listenToMatchStatusEvents(matchUuid = matchUuid)
    }

    fun sendParticipantsToMatch() {
        runSync {
            sendMessage("queue.success.match_ready", this.serverSlug)
            participantUuids.forEach { playerUuid ->
                Bukkit.getPlayer(playerUuid)?.let { player ->
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "transfer ${this.serverSlug}.${Config.getMinecraftServerIP()} 25565 ${player.name}")
                }
            }
            LobbyService.deleteLobby()
        }
    }

    fun getLobbyAudience(): Audience {
        return Audience.audience(this.participantUuids.mapNotNull { Bukkit.getPlayer(it) })
    }

    private fun getLobbyAudienceWithout(uuidsToExclude: Collection<UUID>): Audience {
        return Audience.audience(this.participantUuids
            .filterNot { it in uuidsToExclude }
            .mapNotNull { Bukkit.getPlayer(it) })
    }
}