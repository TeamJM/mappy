package info.journeymap.mappy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Channels(
    @SerialName("bot_commands")
    val botCommands: Int = 0,

    @SerialName("bot_logs")
    val botLogs: Int = 0,

    val checkpoint: Int = 0
)


@Serializable
data class Roles(
    val owners: Int = 0,
    val admins: Int = 0,
    val moderators: Int = 0,

    val muted: Int = 0,
    val verified: Int = 0
)


@Serializable
data class Config (
    val token: String = "",

    @SerialName("server_id")
    val serverId: Int = 0,

    val channels: Channels = Channels(),

    @SerialName("staff_channels")
    val staffChannels: List<Int> = listOf(0),

    val roles: Roles = Roles()
)