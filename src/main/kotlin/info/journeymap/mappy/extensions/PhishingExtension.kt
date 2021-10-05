package info.journeymap.mappy.extensions

import com.kotlindiscord.kord.extensions.DISCORD_RED
import com.kotlindiscord.kord.extensions.DiscordRelayedException
import com.kotlindiscord.kord.extensions.checks.topRoleHigherOrEqual
import com.kotlindiscord.kord.extensions.checks.topRoleLower
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralMessageCommand
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.getJumpUrl
import dev.kord.core.behavior.ban
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.message.MessageCreateEvent
import info.journeymap.mappy.PhishingApi
import info.journeymap.mappy.config.config
import info.journeymap.mappy.defaultCheck
import info.journeymap.mappy.enums.Roles
import kotlinx.coroutines.flow.firstOrNull
import mu.KotlinLogging

val domainRegex = "([^\\s`\"'<>/]+\\s*(?:\\.|dot)+\\s*[^\\s`\"'<>/]+)".toRegex(RegexOption.IGNORE_CASE)

class PhishingExtension : Extension() {
    override val name: String = "phishing"
    private val logger = KotlinLogging.logger { }

    val api = PhishingApi()

    override suspend fun setup() {
        event<MessageCreateEvent> {
            check(defaultCheck)
            check { topRoleLower { config.getRole(Roles.MODERATOR) } }

            action {
                if (hasBadDomain(event.message)) {
                    event.message.author!!.asMember(event.message.getGuild().id).ban {
                        reason = "Posted a phishing domain."
                        deleteMessagesDays = 1
                    }

                    event.getGuild()?.getLogsChannel()?.createEmbed {
                        title = "Scam domain detected"
                        color = DISCORD_RED
                        description = event.message.content

                        field {
                            name = "Author"
                            value = "${event.message.author!!.mention} (" +
                                    "`${event.message.author!!.id.asString}` / " +
                                    "`${event.message.author!!.tag}`" +
                                    ")"
                        }

                        field {
                            name = "Channel"
                            value = "${event.message.channel.mention} (" +
                                    "`${event.message.channel.id.asString}` / " +
                                    ")"
                        }

                        field {
                            name = "Message"
                            value = "[`${event.message.id.asString}`](${event.message.getJumpUrl()})"
                        }
                    }
                }
            }
        }

        ephemeralSlashCommand(::DomainArgs) {
            name = "phishing-check"
            description = "Check whether a given domain is a known phishing domain."

            check { topRoleHigherOrEqual { config.getRole(Roles.MODERATOR) } }

            action {
                respond {
                    content = if (api.checkDomain(arguments.domain)) {
                        "✅ `${arguments.domain}` is a known phishing domain."
                    } else {
                        "❌ `${arguments.domain}` is not a known phishing domain."
                    }
                }
            }
        }

        ephemeralMessageCommand {
            name = "Phishing Check"

            check { topRoleHigherOrEqual { config.getRole(Roles.MODERATOR) } }

            action {
                respond {
                    content = if (targetMessages.any { hasBadDomain(it) }) {
                        "✅ this message contains a known phishing domain."
                    } else {
                        "❌ this message does not contain a known phishing domain."
                    }
                }
            }
        }
    }

    suspend inline fun hasBadDomain(content: String): Boolean =
        extractUrlInfo(content).any { domain ->
            api.checkDomain(domain)
        }

    suspend inline fun hasBadDomain(message: Message): Boolean = hasBadDomain(message.content)

    fun extractUrlInfo(content: String): Set<String> {
        val found: MutableSet<String> = mutableSetOf()

        for (match in domainRegex.findAll(content)) {
            found.add(match.groups[1]!!.value)
        }

        logger.debug { "Matches (${found.size}): ${found.joinToString()}" }

        return found
    }

    suspend fun Guild.getLogsChannel() =
        channels.firstOrNull { it.name == "logs" }
            ?.asChannelOrNull() as? GuildMessageChannel

    inner class DomainArgs : Arguments() {
        val domain by string("domain", "Domain name to check") { _, value ->
            if ("/" in value) {
                throw DiscordRelayedException("Please provide the domain name only, without the protocol or a path.")
            }
        }
    }
}
