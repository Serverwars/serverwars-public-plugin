package net.serverwars.sunsetPlugin.domain.lobby.models

import net.kyori.adventure.audience.Audience
import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.domain.match.services.MatchDataAccess
import net.serverwars.sunsetPlugin.domain.server.services.ServerService
import net.serverwars.sunsetPlugin.translations.sendTranslatedActionBarMessage
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import net.serverwars.sunsetPlugin.util.now
import net.serverwars.sunsetPlugin.util.playMatchFoundSound
import net.serverwars.sunsetPlugin.util.runSync
import org.bukkit.Bukkit
import java.util.*

data class Lobby(
    private val lobbyUuid: UUID,
    private val participants: List<Participant>,
    private val createdAtTimestamp: Long,
    private val lobbySettings: LobbySettings,
    private val invitations: List<LobbyInvitation>,
) {
    companion object {
        val ALLOWED_LOBBY_SIZES = 1..10

        fun create(lobbySettings: LobbySettings): Lobby {
            return Lobby(
                lobbyUuid = UUID.randomUUID(),
                participants = emptyList(),
                createdAtTimestamp = now(),
                lobbySettings = lobbySettings,
                invitations = emptyList(),
            )
        }
    }

    fun hasParticipant(participant: Participant): Boolean = participants
        .map { it.playerUuid }
        .contains(participant.playerUuid)

    fun getParticipantAmount() = participants.size

    fun getParticipants(): List<Participant> = participants

    fun getLobbySettings(): LobbySettings = lobbySettings

    fun getInvitationForPlayer(playerUuid: UUID): LobbyInvitation? = invitations
        .find { it.inviteeUuid == playerUuid }

    fun withLobbySettings(lobbySettings: LobbySettings): Lobby {
        return this.copy(lobbySettings = lobbySettings)
    }

    fun withParticipant(participant: Participant): Lobby {
        return this.copy(participants = this.participants.plus(participant))
    }

    fun withoutParticipant(participant: Participant): Lobby {
        return this.copy(participants = this.participants.minus(participant))
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
            val serverSlug = ServerService.getServerSlug()
            sendMessage("queue.success.match_ready", serverSlug)

            val shouldAutoTransferPlayers = Config.shouldTransferOnMatchReady()
            if (shouldAutoTransferPlayers) {
                participants.forEach { (_, name) ->
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "transfer $serverSlug.${Config.getServerwarsMinecraftServerIP()} 25565 $name")
                }
            }

            LobbyService.deleteLobby()
        }
    }

    fun getLobbyAudience(): Audience {
        return Audience.audience(this.participants.mapNotNull { Bukkit.getPlayer(it.playerUuid) })
    }

    private fun getLobbyAudienceWithout(uuidsToExclude: Collection<UUID>): Audience {
        return Audience.audience(this.participants
            .filterNot { it.playerUuid in uuidsToExclude }
            .mapNotNull { Bukkit.getPlayer(it.playerUuid) })
    }
}