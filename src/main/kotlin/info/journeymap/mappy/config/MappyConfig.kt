package info.journeymap.mappy.config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.Feature
import com.uchuhimo.konf.source.toml
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Role
import dev.kord.core.entity.channel.Channel
import info.journeymap.mappy.MissingChannelException
import info.journeymap.mappy.MissingGuildException
import info.journeymap.mappy.MissingRoleException
import info.journeymap.mappy.config.spec.*
import info.journeymap.mappy.enums.Channels
import info.journeymap.mappy.enums.Colours
import info.journeymap.mappy.enums.Roles
import info.journeymap.mappy.enums.Titles
import org.koin.core.component.KoinComponent
import java.awt.Color
import java.io.File

/**
 * Class representing the bot's configuration.
 */
class MappyConfig : KoinComponent {
    private var config = Config {
        addSpec(BotSpec)
        addSpec(ChannelsSpec)
        addSpec(ColourSpec)
        addSpec(EmbedSpec)
        addSpec(FilterSpec)
        addSpec(RolesSpec)
    }
        .from.enabled(Feature.FAIL_ON_UNKNOWN_PATH).toml.resource("default.toml")
        .from.env()
        .from.systemProperties()

    init {
        if (File("config.toml").exists()) {
            config = config.from.toml.watchFile("config.toml")
        }
    }

    /**
     * The bot's login token.
     */
    val token: String get() = config[BotSpec.token]

    /**
     * The bot's command prefix.
     */
    val prefix: String get() = config[BotSpec.commandPrefix]

    /**
     * The [Snowflake] object representing the bot's configured primary guild.
     */
    val guildSnowflake: Snowflake get() = Snowflake(config[BotSpec.guild])

    /** Whether to use the StopModReposts blacklist. **/
    val useStopModReposts: Boolean get() = config[FilterSpec.stopModReposts]

    /** Banned adult domains. **/
    val adultDomains: List<String> get() = config[FilterSpec.DomainsSpec.adult]

    /** Banned adult domains. **/
    val antiPrivacyDomains: List<String> get() = config[FilterSpec.DomainsSpec.antiPrivacy]

    /** Banned adult domains. **/
    val objectionableDomains: List<String> get() = config[FilterSpec.DomainsSpec.objectionable]

    /**
     * Get a Set containing all the configured banned domains.
     *
     * This does not include any StopModReposts domains.
     *
     * @return The set of banned domains.
     */
    fun getBannedDomains(): Set<String> =
        adultDomains.toSet() + antiPrivacyDomains.toSet() + objectionableDomains.toSet()

    /**
     * Given a [Channels] enum value, attempt to retrieve the corresponding Discord [Channel]
     * object.
     *
     * @param channel The corresponding [Channels] enum value to retrieve the channel for.
     * @return The [Channel] object represented by the given [Channels] enum value.
     * @throws MissingChannelException Thrown if the configured [Channel] cannot be found.
     */
    @Throws(MissingChannelException::class)
    suspend fun getChannel(channel: Channels): Channel {
        val kord = getKoin().get<Kord>()

        val snowflake = when (channel) {
            Channels.BOT_COMMANDS -> Snowflake(config[ChannelsSpec.botCommands])
            Channels.BOT_LOGS     -> Snowflake(config[ChannelsSpec.botLogs])
            Channels.MODS         -> Snowflake(config[ChannelsSpec.mods])
            Channels.INFO         -> Snowflake(config[ChannelsSpec.info])
        }

        return kord.getChannel(snowflake) ?: throw MissingChannelException(snowflake.value)
    }

    /**
     * Given a [Roles] enum value, retrieve a [Snowflake] corresponding with a configured role.
     *
     * @param role The corresponding [Roles] enum value to retrieve the [Snowflake] for.
     * @return The [Snowflake] object represented by the given [Roles] enum value.
     */
    fun getRoleSnowflake(role: Roles): Snowflake {
        return when (role) {
            Roles.OWNER         -> Snowflake(config[RolesSpec.owner])
            Roles.ADMIN         -> Snowflake(config[RolesSpec.admin])
            Roles.MODERATOR     -> Snowflake(config[RolesSpec.moderator])
            Roles.BOT           -> Snowflake(config[RolesSpec.bot])
            Roles.MUTED         -> Snowflake(config[RolesSpec.muted])
            Roles.ANNOUNCEMENTS -> Snowflake(config[RolesSpec.announcements])
        }
    }

    /**
     * Given a [Roles] enum value, attempt to retrieve the corresponding Discord [Role]
     * object.
     *
     * @param role The corresponding [Roles] enum value to retrieve the channel for.
     * @return The [Role] object represented by the given [Roles] enum value.
     * @throws MissingRoleException Thrown if the configured [Role] cannot be found.
     */
    @Throws(MissingRoleException::class)
    suspend fun getRole(role: Roles): Role {
        val snowflake = getRoleSnowflake(role)

        return getGuild().getRoleOrNull(snowflake) ?: throw MissingRoleException(snowflake.value)
    }

    /**
     * Attempt to retrieve the [Guild] object for the configured primary guild.
     *
     * @return The [Guild] object representing the configured primary guild.
     * @throws MissingGuildException Thrown if the configured [Guild] cannot be found.
     */
    @Throws(MissingGuildException::class)
    suspend fun getGuild(): Guild {
        val kord = getKoin().get<Kord>()

        return kord.getGuild(guildSnowflake) ?: throw MissingGuildException(guildSnowflake.value)
    }

    /**
     * Given a [Colours] enum value, attempt to retrieve the corresponding [Color] object.
     *
     * @param colour The corresponding [Colours] enum value to retrieve the colour for.
     * @return The [Color] object represented by the given [Colours] enum value.
     */
    fun getColour(colour: Colours): Color = when (colour) {
        Colours.RED     -> Color.decode(config[ColourSpec.red].toString())
        Colours.GREEN   -> Color.decode(config[ColourSpec.green].toString())
        Colours.BLURPLE -> Color.decode("#7289DA")  // Discord blurple
    }

    /**
     * Given a [Titles] enum value, attempt to retrieve a random [String] embed title.
     *
     * @param title The corresponding [Titles] enum value to retrieve a title for.
     * @return The random title from the list corresponding to the given [Titles] enum value.
     */
    fun getTitle(title: Titles): String = when (title) {
        Titles.POSITIVE -> config[EmbedSpec.positiveTitles].random()
        Titles.NEGATIVE -> config[EmbedSpec.negativeTitles].random()
    }
}

/**
 * The currently loaded [MappyConfig].
 *
 * You should always use this instead of constructing an instance of [MappyConfig] yourself.
 */
val config = MappyConfig()
