package info.journeymap.mappy.extensions

import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.core.event.message.MessageUpdateEvent
import com.gitlab.kordlib.rest.request.RestRequestException
import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.checks.topRoleLower
import com.kotlindiscord.kord.extensions.extensions.Extension
import info.journeymap.mappy.*
import info.journeymap.mappy.config.config
import info.journeymap.mappy.enums.Channels
import info.journeymap.mappy.enums.Roles
import info.journeymap.mappy.mod_reposts.modReposts
import io.ktor.http.*
import mu.KotlinLogging
import org.nibor.autolink.LinkExtractor
import org.nibor.autolink.LinkType

private val logger = KotlinLogging.logger {}

/** How long to wait before removing notification messages in channels - 10 seconds. **/
const val DELETE_DELAY = 10_000L

/**
 * Filter in charge of filtering out bad messages (based on URLs right now).
 *
 * This is loosely based on the filter extension in Kotlin Discord's bot.
 */
class FilterExtension(bot: ExtensibleBot) : Extension(bot) {
    override val name: String = "filter"

    private val extractor = LinkExtractor.builder()
        .linkTypes(setOf(LinkType.URL, LinkType.WWW))
        .build()

    private val mistakeMessage = "If you feel that this was a mistake, please feel free to contact a member of staff."

    private var bannedDomains = config.getBannedDomains()

    init {
        if (config.useStopModReposts) {
            bannedDomains = bannedDomains + modReposts.domains.values.map { it.domain }.toSet()
        }
    }

    override suspend fun setup() {
        event<MessageCreateEvent> {
            check(
                ::defaultCheck,
                topRoleLower(config.getRole(Roles.MODERATOR))
            )

            action {
                doFilter(it.message)
            }
        }

        event<MessageUpdateEvent> {
            check(
                ::defaultCheck,
                topRoleLower(config.getRole(Roles.MODERATOR))
            )

            action {
                doFilter(it.getMessageOrNull() ?: return@action)  // If null, it's been deleted
            }
        }

        logger.info { "Loaded filter extension:" }

        logger.info { "${config.adultDomains.size} adult domains" }
        logger.info { "${config.antiPrivacyDomains.size} anti-privacy domains" }
        logger.info { "${config.objectionableDomains.size} objectionable domains" }

        if (config.useStopModReposts) {
            logger.info { "${modReposts.domains.size} mod-reposting domains" }
        }
    }

    private suspend fun doFilter(message: Message) {
        val domains = extractUrlInfo(message.content)

        if (domains.isNotEmpty()) {
            message.deleteIgnoringNotFound()

            (config.getChannel(Channels.BOT_LOGS) as TextChannel).createEmbed {
                title = "URL filter triggered!"
                description = getMessage(message.author!!, message, message.getChannel())
            }

            sendNotification(
                message,
                "Your link has been removed, as it references a blacklisted URL scheme, domain or domain " +
                        "extension."
            )
        }
    }

    private suspend fun getMessage(user: User, message: Message, channel: Channel): String {
        val channelMessage = if (channel.type == ChannelType.GuildText) {
            "in ${channel.mention}"
        } else {
            "in a DM"
        }

        val jumpMessage = if (channel.type == ChannelType.GuildText) {
            "[the following message](https://discordapp.com/channels/" +
                    "${message.getGuild().id.value}/${channel.id}/${message.id})"
        } else {
            "the following message"
        }

        return "Domain filter triggered by " +
                "**${user.username}#${user.discriminator}** (`${user.id.value}`) $channelMessage, " +
                "with $jumpMessage:\n\n" +
                message.content
    }

    private fun extractUrlInfo(content: String): Set<Pair<String?, String>> {
        val links = extractor.extractLinks(content)
        val badPairs: MutableList<Pair<String?, String>> = mutableListOf()
        val foundPairs: MutableList<Pair<String?, String>> = mutableListOf()

        for (link in links) {
            foundPairs += Pair(
                link.getScheme(content),
                link.getDomain(content)
            )
        }

        for (ending in bannedDomains) {
            for ((scheme, domain) in foundPairs) {
                if (domain.endsWith(ending)) {
                    badPairs += Pair(scheme, domain)
                }
            }
        }

        return badPairs.toSet()
    }

    /**
     * Send a notification to a user - attempting to DM first, and then using a channel.
     *
     * @param eventMessage Message object from the event corresponding with this filtering attempt
     * @param reason Human-readable reason to send to the user
     */
    private suspend fun sendNotification(eventMessage: Message, reason: String): Message {
        val message = "$reason\n\n$mistakeMessage"

        try {
            val channel = eventMessage.author!!.getDmChannel()

            return channel.createMessage(message)
        } catch (e: RestRequestException) {
            if (e.code == HttpStatusCode.Forbidden.value) {
                val notificationMessage =
                    eventMessage.channel.createMessage("${eventMessage.author!!.mention} $message")

                notificationMessage.deleteWithDelay(DELETE_DELAY)

                return notificationMessage
            }

            throw e
        }
    }
}
