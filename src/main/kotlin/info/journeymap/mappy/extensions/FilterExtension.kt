package info.journeymap.mappy.extensions

import com.kotlindiscord.kord.extensions.checks.topRoleLower
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import dev.kord.common.entity.ChannelType
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.event.message.MessageUpdateEvent
import dev.kord.rest.request.RestRequestException
import info.journeymap.mappy.*
import info.journeymap.mappy.config.config
import info.journeymap.mappy.enums.Channels
import info.journeymap.mappy.enums.Roles
import info.journeymap.mappy.stopmodreposts.modReposts
import io.ktor.http.*
import mu.KotlinLogging
import org.nibor.autolink.LinkExtractor
import org.nibor.autolink.LinkType

private val logger = KotlinLogging.logger {}

/** How long to wait before removing notification messages in channels - 10 seconds. **/
private const val DELETE_DELAY = 10_000L

/**
 * Filter in charge of filtering out bad messages (based on URLs right now).
 *
 * This is loosely based on the filter extension in Kotlin Discord's bot.
 */
class FilterExtension : Extension() {
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
            check(defaultCheck)
            check { topRoleLower { config.getRole(Roles.MODERATOR) } }

            action {
                doFilter(event.message)
            }
        }

        event<MessageUpdateEvent> {
            check(defaultCheck)
            check { topRoleLower { config.getRole(Roles.MODERATOR) } }

            action {
                doFilter(event.getMessageOrNull() ?: return@action)  // If null, it's been deleted
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
            if (e.status.code == HttpStatusCode.Forbidden.value) {
                val notificationMessage =
                    eventMessage.channel.createMessage("${eventMessage.author!!.mention} $message")

                notificationMessage.deleteWithDelay(DELETE_DELAY)

                return notificationMessage
            }

            throw e
        }
    }
}
