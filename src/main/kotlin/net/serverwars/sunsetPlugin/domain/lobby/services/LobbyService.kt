package net.serverwars.sunsetPlugin.domain.lobby.services

import net.serverwars.sunsetPlugin.domain.lobby.exceptions.CreateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.DeleteLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.SendLobbyToMatchException
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbyAccessType
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbyInvitation
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbySettings
import net.serverwars.sunsetPlugin.domain.lobby.models.Participant
import net.serverwars.sunsetPlugin.domain.queue.exceptions.LeaveQueueException
import net.serverwars.sunsetPlugin.domain.queue.services.QueueService
import net.serverwars.sunsetPlugin.util.*
import java.util.UUID
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

    fun createLobby(size: Int, accessType: LobbyAccessType, gameType: String): Lobby {
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

        if (lobbyValue.getLobbySettings().size == value) {
            throw UpdateLobbyException("command.lobby.set.error.nothing_changed")
        }

        playSetLobbySound(lobbyValue)
        val updatedLobbySettings = lobbyValue.getLobbySettings().withSize(value)
        val updatedLobby = lobbyValue.withLobbySettings(updatedLobbySettings)
        lobby = updatedLobby
        return updatedLobby
    }

    fun updateLobbyAccessType(value: LobbyAccessType): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.set.error.no_lobby")

        if (lobbyValue.getLobbySettings().accessType == value) {
            throw UpdateLobbyException("command.lobby.set.error.nothing_changed")
        }

        playSetLobbySound(lobbyValue)
        val updatedLobbySettings = lobbyValue.getLobbySettings().withAccessType(value)
        val updatedLobby = lobbyValue.withLobbySettings(updatedLobbySettings).withoutInvites()
        lobby = updatedLobby
        return updatedLobby
    }

    fun updateLobbyGameType(value: String): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.set.error.no_lobby")

        if (lobbyValue.getLobbySettings().gameType == value) {
            throw UpdateLobbyException("command.lobby.set.error.nothing_changed")
        }

        playSetLobbySound(lobbyValue)
        val updatedLobbySettings = lobbyValue.getLobbySettings().withGameType(value)
        val updatedLobby = lobbyValue.withLobbySettings(updatedLobbySettings)
        lobby = updatedLobby
        return updatedLobby
    }

    fun playerJoinLobby(playerUuid: UUID, force: Boolean = false): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.join.error.no_lobby")
        val participant = Participant.create(playerUuid)

        if (lobbyValue.hasParticipant(participant)) {
            throw UpdateLobbyException("command.lobby.join.error.already_in_lobby")
        }

        if (lobbyValue.getParticipantAmount() >= lobbyValue.getLobbySettings().size) {
            throw UpdateLobbyException("command.lobby.join.error.lobby_full")
        }

        if (!force && lobbyValue.getLobbySettings().accessType == LobbyAccessType.INVITE_ONLY) {
            return acceptLobbyInvitation(playerUuid)
        }

        val updatedLobby = lobbyValue.withParticipant(participant)
        lobby = updatedLobby
        playPlayerJoinsLobbySound(updatedLobby)
        return updatedLobby
    }

    fun participantLeaveLobby(participant: Participant): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.leave.error.no_lobby")

        if (!lobbyValue.hasParticipant(participant)) {
            throw UpdateLobbyException("command.lobby.leave.error.not_in_lobby")
        }

        playPlayerLeavesLobbySound(lobbyValue)
        val updatedLobby = lobbyValue.withoutParticipant(participant)
        lobby = updatedLobby
        return updatedLobby
    }

    fun kickParticipantFromLobby(kickedParticipant: Participant): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.kick.error.no_lobby")

        if (!lobbyValue.hasParticipant(kickedParticipant)) {
            throw UpdateLobbyException("command.lobby.kick.error.not_in_lobby")
        }

        playPlayerKickedFromLobbySound(lobbyValue)
        val updatedLobby = lobbyValue.withoutParticipant(kickedParticipant)
        lobby = updatedLobby
        return updatedLobby
    }

    fun createLobbyInvitation(inviterParticipant: Participant, invitedPlayerUuid: UUID): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.invite.error.no_lobby")

        if (!lobbyValue.hasParticipant(inviterParticipant)) {
            throw UpdateLobbyException("command.lobby.invite.error.inviter_not_in_lobby")
        }

        if (inviterParticipant == invitedPlayerUuid) {
            throw UpdateLobbyException("command.lobby.invite.error.self_invite")
        }

        val invitedParticipant = Participant.create(invitedPlayerUuid)
        if (lobbyValue.hasParticipant(invitedParticipant)) {
            throw UpdateLobbyException("command.lobby.invite.error.invitee_already_in_lobby")
        }

        if (lobbyValue.getInvitationForPlayer(invitedPlayerUuid) != null) {
            throw UpdateLobbyException("command.lobby.invite.error.invitee_already_invited")
        }

        if (lobbyValue.getParticipantAmount() >= lobbyValue.getLobbySettings().size) {
            throw UpdateLobbyException("command.lobby.invite.error.lobby_full")
        }

        playCreateInviteSound(lobbyValue)
        val newInvitation = LobbyInvitation.create(invitedPlayerUuid)
        val updatedLobby = lobbyValue.withInvite(newInvitation)
        lobby = updatedLobby

        return updatedLobby
    }

    fun revokeLobbyInvitation(invitedPlayerUuid: UUID): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.uninvite.error.no_lobby")
        val invitedParticipant = Participant.create(invitedPlayerUuid)

        if (lobbyValue.hasParticipant(invitedParticipant)) {
            throw UpdateLobbyException("command.lobby.uninvite.error.uninvitee_already_in_lobby")
        }

        val invitation = lobbyValue.getInvitationForPlayer(invitedPlayerUuid)
            ?: throw UpdateLobbyException("command.lobby.uninvite.error.uninvitee_not_invited")

        playRevokeInviteSound(lobbyValue)
        val updatedLobby = lobbyValue.withoutInvite(invitation)
        lobby = updatedLobby
        return updatedLobby
    }

    fun acceptLobbyInvitation(invitedPlayerUuid: UUID): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.invite.accept.error.no_lobby")

        if (lobbyValue.getParticipantAmount() >= lobbyValue.getLobbySettings().size) {
            throw UpdateLobbyException("command.lobby.invite.accept.error.lobby_full")
        }

        val invitedParticipant = Participant.create(invitedPlayerUuid)
        if (lobbyValue.hasParticipant(invitedParticipant)) {
            throw UpdateLobbyException("command.lobby.invite.accept.error.already_joined")
        }

        val invitation = lobbyValue.getInvitationForPlayer(invitedPlayerUuid)
            ?: throw UpdateLobbyException("command.lobby.invite.accept.error.not_invited")

        val updatedLobby = lobbyValue.withoutInvite(invitation).withParticipant(invitedParticipant)
        lobby = updatedLobby
        playPlayerJoinsLobbySound(updatedLobby)
        return updatedLobby
    }

    fun declineLobbyInvitation(invitedPlayerUuid: UUID): Lobby {
        val lobbyValue = this.lobby
            ?: throw UpdateLobbyException("command.lobby.invite.decline.error.no_lobby")
        val invitation = lobbyValue.getInvitationForPlayer(invitedPlayerUuid)
            ?: throw UpdateLobbyException("command.lobby.invite.decline.error.not_invited")
        val invitedParticipant = Participant.create(invitedPlayerUuid)

        if (lobbyValue.hasParticipant(invitedParticipant)) {
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