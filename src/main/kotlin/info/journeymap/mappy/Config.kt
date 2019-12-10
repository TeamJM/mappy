package info.journeymap.mappy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Channels(
    @SerialName("bot_commands")
    val botCommands: Long = System.getenv("CHANNEL_COMMANDS")?.toLong() ?: 0L,

    @SerialName("bot_logs")
    val botLogs: Long = System.getenv("CHANNEL_LOGS")?.toLong() ?: 0L,

    val checkpoint: Long = System.getenv("CHANNEL_CHECKPOINT")?.toLong() ?: 0L
)


@Serializable
data class Roles(
    val owners: Long = System.getenv("ROLE_OWNERS")?.toLong() ?: 0L,
    val admins: Long = System.getenv("ROLE_ADMINS")?.toLong() ?: 0L,
    val moderators: Long = System.getenv("ROLE_MODERATORS")?.toLong() ?: 0L,

    val muted: Long = System.getenv("ROLE_MUTED")?.toLong() ?: 0L,
    val verified: Long = System.getenv("ROLE_VERIFIED")?.toLong() ?: 0L
)


@Serializable
data class Config(
    val token: String = System.getenv("TOKEN") ?: "",

    @SerialName("owner_id")
    val ownerId: Long = System.getenv("OWNER_ID")?.toLong() ?: 0,

    @SerialName("server_id")
    val serverId: Long = System.getenv("SERVER_ID")?.toLong() ?: 0L,

    val channels: Channels = Channels(),

    @SerialName("staff_channels")
    val staffChannels: List<Long> = System.getenv("STAFF_CHANNELS")?.split(",")?.map { it.toLong() } ?: listOf(0L),

    val roles: Roles = Roles()
)