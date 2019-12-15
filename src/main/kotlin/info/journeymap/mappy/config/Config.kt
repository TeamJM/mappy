package info.journeymap.mappy.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Channels(
    @SerialName("bot_commands")
    val botCommands: Long = System.getenv("CHANNEL_COMMANDS")?.toLong() ?: 0L,

    @SerialName("bot_logs")
    val botLogs: Long = System.getenv("CHANNEL_LOGS")?.toLong() ?: 0L,

    val checkpoint: Long = System.getenv("CHANNEL_CHECKPOINT")?.toLong() ?: 0L,
    val info: Long = System.getenv("CHANNEL_INFO")?.toLong() ?: 0L
)


@Serializable
data class Colours(
    val green: Int = System.getenv("COLOUR_GREEN")?.toInt() ?: 0x169718,
    val red: Int = System.getenv("COLOUR_RED")?.toInt() ?: 0x961917
)


@Serializable
data class EmbedTitles(
    @SerialName("negative_titles")
    val negativeTitles: List<String> = defaultNegativeTitles,

    @SerialName("positive_titles")
    val positiveTitles: List<String> = defaultPositiveTitles
)


@Serializable
data class Roles(
    val owners: Long = System.getenv("ROLE_OWNERS")?.toLong() ?: 0L,
    val admins: Long = System.getenv("ROLE_ADMINS")?.toLong() ?: 0L,
    val moderators: Long = System.getenv("ROLE_MODERATORS")?.toLong() ?: 0L,
    val bots: Long = System.getenv("ROLE_BOTS")?.toLong() ?: 0L,

    val announcements: Long = System.getenv("ROLE_ANNOUNCEMENTS")?.toLong() ?: 0L,
    val muted: Long = System.getenv("ROLE_MUTED")?.toLong() ?: 0L,
    val verified: Long = System.getenv("ROLE_VERIFIED")?.toLong() ?: 0L
) {
    @Transient
    var staffRoles: List<Long> = listOf()

    init {
        if (this.staffRoles.isEmpty()) {
            staffRoles = listOf(this.owners, this.admins, this.moderators, this.bots)
        }
    }
}


@Serializable
data class Config(
    val token: String = System.getenv("TOKEN") ?: "",

    @SerialName("owner_id")
    val ownerId: Long = System.getenv("OWNER_ID")?.toLong() ?: 0,

    @SerialName("server_id")
    val serverId: Long = System.getenv("SERVER_ID")?.toLong() ?: 0L,

    val channels: Channels = Channels(),

    @SerialName("staff_channels")
    val staffChannels: List<Long> = System.getenv("STAFF_CHANNELS")?.split(",")?.map { it.toLong() }?.toList()
        ?: listOf(0L),

    val colours: Colours = Colours(),

    @SerialName("embed_titles")
    val embedTitles: EmbedTitles = EmbedTitles(),

    val roles: Roles = Roles()
)