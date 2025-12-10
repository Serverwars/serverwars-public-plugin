package net.serverwars.sunsetPlugin.domain.lobby.services

import net.serverwars.sunsetPlugin.domain.gameserver.models.GameServerType
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.CreateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.DeleteLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.SendLobbyToMatchException
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbyAccessType
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbyInvitation
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbySettings
import net.serverwars.sunsetPlugin.domain.queue.exceptions.LeaveQueueException
import net.serverwars.sunsetPlugin.domain.queue.services.QueueService
import net.serverwars.sunsetPlugin.util.*
import java.util.*
import kotlin.properties.Delegates

object LobbyService {

    private var lobby: Lobby? by Delegates.observable(null as Lobby?) { _, _, _ ->
        runAsync {
            try {
                QueueService.leaveQueue()
            } catch (_: LeaveQueueException) {
            }
        }
    }

    fun createLobby(size: Int, accessType: LobbyAccessType, gameType: GameServerType): Lobby {
        if (lobby != null) {
            throw CreateLobbyException("command.lobby.create.error.too_many_existing_lobbies")
        }

        if (!Lobby.ALLOWED_LOBBY_SIZES.contains(size)) {
            throw CreateLobbyException("command.lobby.create.error.invalid_size", size)
        }

        val newLobbySettings = LobbySettings.create(size, accessType, gameType)
        val newLobby = Lobby.create(newLobbySettings)
        this.lobby = newLobby

        return newLobby
    }

    fun updateLobbySize(value: Int): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.set.error.no_lobby")

        if (!Lobby.ALLOWED_LOBBY_SIZES.contains(value)) {
            throw UpdateLobbyException("command.lobby.error.invalid_size", value)
        }

        if (lobbyValue.lobbySettings.size == value) {
            throw UpdateLobbyException("command.lobby.set.error.nothing_changed")
        }

        playSetLobbySound(lobbyValue)
        val updatedLobbySettings = lobbyValue.lobbySettings.withSize(value)
        val updatedLobby = lobbyValue.withLobbySettings(updatedLobbySettings)
        lobby = updatedLobby
        return updatedLobby
    }

    fun updateLobbyAccessType(value: LobbyAccessType): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.set.error.no_lobby")

        if (lobbyValue.lobbySettings.accessType == value) {
            throw UpdateLobbyException("command.lobby.set.error.nothing_changed")
        }

        playSetLobbySound(lobbyValue)
        val updatedLobbySettings = lobbyValue.lobbySettings.withAccessType(value)
        val updatedLobby = lobbyValue.withLobbySettings(updatedLobbySettings).withoutInvites()
        lobby = updatedLobby
        return updatedLobby
    }

    fun updateLobbyGameType(value: GameServerType): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.set.error.no_lobby")

        if (lobbyValue.lobbySettings.gameType == value) {
            throw UpdateLobbyException("command.lobby.set.error.nothing_changed")
        }

        playSetLobbySound(lobbyValue)
        val updatedLobbySettings = lobbyValue.lobbySettings.withGameType(value)
        val updatedLobby = lobbyValue.withLobbySettings(updatedLobbySettings)
        lobby = updatedLobby
        return updatedLobby
    }

    fun playerJoinLobby(playerUuid: UUID): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.join.error.no_lobby")

        if (lobbyValue.participantUuids.contains(playerUuid)) {
            throw UpdateLobbyException("command.lobby.join.error.already_in_lobby")
        }

        if (lobbyValue.participantUuids.size >= lobbyValue.lobbySettings.size) {
            throw UpdateLobbyException("command.lobby.join.error.lobby_full")
        }

        if (lobbyValue.lobbySettings.accessType == LobbyAccessType.INVITE_ONLY) {
            return acceptLobbyInvitation(playerUuid)
        }

        val updatedLobby = lobbyValue.withPlayer(playerUuid)
        lobby = updatedLobby
        playPlayerJoinsLobbySound(updatedLobby)
        return updatedLobby
    }

    fun playerLeaveLobby(playerUuid: UUID): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.leave.error.no_lobby")

        if (!lobbyValue.participantUuids.contains(playerUuid)) {
            throw UpdateLobbyException("command.lobby.leave.error.not_in_lobby")
        }

        playPlayerLeavesLobbySound(lobbyValue)
        val updatedLobby = lobbyValue.withoutPlayer(playerUuid)
        lobby = updatedLobby
        return updatedLobby
    }

    fun playerKickLobby(kickeeUuid: UUID): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.kick.error.no_lobby")

        if (!lobbyValue.participantUuids.contains(kickeeUuid)) {
            throw UpdateLobbyException("command.lobby.kick.error.not_in_lobby")
        }

        playPlayerKickedFromLobbySound(lobbyValue)
        val updatedLobby = lobbyValue.withoutPlayer(kickeeUuid)
        lobby = updatedLobby
        return updatedLobby
    }

    fun createLobbyInvitation(inviter: UUID, invitee: UUID): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.invite.error.no_lobby")

        if (!lobbyValue.participantUuids.contains(inviter)) {
            throw UpdateLobbyException("command.lobby.invite.error.inviter_not_in_lobby")
        }

        if (inviter == invitee) {
            throw UpdateLobbyException("command.lobby.invite.error.self_invite")
        }

        if (lobbyValue.participantUuids.contains(invitee)) {
            throw UpdateLobbyException("command.lobby.invite.error.invitee_already_in_lobby")
        }

        if (lobbyValue.invitations.map { it.inviteeUuid }.contains(invitee)) {
            throw UpdateLobbyException("command.lobby.invite.error.invitee_already_invited")
        }

        if (lobbyValue.participantUuids.size >= lobbyValue.lobbySettings.size) {
            throw UpdateLobbyException("command.lobby.invite.error.lobby_full")
        }

        playCreateInviteSound(lobbyValue)
        val newInvitation = LobbyInvitation.create(invitee)
        val updatedLobby = lobbyValue.withInvite(newInvitation)
        lobby = updatedLobby

        return updatedLobby
    }

    fun revokeLobbyInvitation(playerUuid: UUID): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.uninvite.error.no_lobby")

        if (lobbyValue.participantUuids.contains(playerUuid)) {
            throw UpdateLobbyException("command.lobby.uninvite.error.uninvitee_already_in_lobby")
        }

        val invitation = lobbyValue.invitations.find { it.inviteeUuid == playerUuid }
            ?: throw UpdateLobbyException("command.lobby.uninvite.error.uninvitee_not_invited")

        playRevokeInviteSound(lobbyValue)
        val updatedLobby = lobbyValue.withoutInvite(invitation)
        lobby = updatedLobby
        return updatedLobby
    }

    fun acceptLobbyInvitation(playerUuid: UUID): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.invite.accept.error.no_lobby")

        if (lobbyValue.participantUuids.size >= lobbyValue.lobbySettings.size) {
            throw UpdateLobbyException("command.lobby.invite.accept.error.lobby_full")
        }

        if (lobbyValue.participantUuids.contains(playerUuid)) {
            throw UpdateLobbyException("command.lobby.invite.accept.error.already_joined")
        }

        val invitation = lobbyValue.invitations.find { it.inviteeUuid == playerUuid }
            ?: throw UpdateLobbyException("command.lobby.invite.accept.error.not_invited")

        val updatedLobby = lobbyValue.withoutInvite(invitation).withPlayer(playerUuid)
        lobby = updatedLobby
        playPlayerJoinsLobbySound(updatedLobby)
        return updatedLobby
    }

    fun declineLobbyInvitation(playerUuid: UUID): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.invite.decline.error.no_lobby")

        val invitation = lobbyValue.invitations.find { it.inviteeUuid == playerUuid }
            ?: throw UpdateLobbyException("command.lobby.invite.decline.error.not_invited")

        if (lobbyValue.participantUuids.contains(playerUuid)) {
            throw UpdateLobbyException("command.lobby.invite.decline.error.already_joined")
        }

        val updatedLobby = lobbyValue.withoutInvite(invitation)
        lobby = updatedLobby
        return updatedLobby
    }

    fun deleteLobby(): Lobby {
        val deletedLobby = this.lobby ?: throw DeleteLobbyException("command.lobby.delete.error.no_lobby")
        this.lobby = null
        return deletedLobby
    }

    fun getLobbyCopy(): Lobby? {
        return this.lobby?.copy()
    }

    fun lobbyExists(): Boolean = this.lobby != null

    fun sendLobbyToMatch() {
        val lobbyValue = this.lobby
            ?: throw SendLobbyToMatchException("Could not send Lobby to match, no lobby found.")

        lobbyValue.sendParticipantsToMatch()
    }

}