package net.serverwars.sunsetPlugin.domain.lobby.models

import net.serverwars.sunsetPlugin.util.now
import java.util.*

data class LobbyInvitation(
    val inviteeUuid: UUID,
    val createdAtTimestamp: Long,
) {
    companion object {
        fun create(inviteeUuid: UUID): LobbyInvitation {
            return LobbyInvitation(
                inviteeUuid = inviteeUuid,
                createdAtTimestamp = now(),
            )
        }
    }
}