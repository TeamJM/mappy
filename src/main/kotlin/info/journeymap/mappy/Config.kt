package info.journeymap.mappy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Channels(
    @SerialName("bot_commands")
    val botCommands: Int = System.getenv("CHANNEL_COMMANDS")?.toInt() ?: 0,

    @SerialName("bot_logs")
    val botLogs: Int = System.getenv("CHANNEL_LOGS")?.toInt() ?: 0,

    val checkpoint: Int = System.getenv("CHANNEL_CHECKPOINT")?.toInt() ?: 0
)


@Serializable
data class Roles(
    val owners: Int = System.getenv("ROLE_OWNERS")?.toInt() ?: 0,
    val admins: Int = System.getenv("ROLE_ADMINS")?.toInt() ?: 0,
    val moderators: Int = System.getenv("ROLE_MODERATORS")?.toInt() ?: 0,

    val muted: Int = System.getenv("ROLE_MUTED")?.toInt() ?: 0,
    val verified: Int = System.getenv("ROLE_VERIFIED")?.toInt() ?: 0
)


@Serializable
data class Config (
    val token: String = System.getenv("TOKEN") ?: "",

    @SerialName("server_id")
    val serverId: Int = System.getenv("SERVER_ID")?.toInt() ?: 0,

    val channels: Channels = Channels(),

    @SerialName("staff_channels")
    val staffChannels: List<Int> = System.getenv("SERVER_ID")?.split(",")?.map { it.toInt() } ?: listOf(0),

    val roles: Roles = Roles()
)