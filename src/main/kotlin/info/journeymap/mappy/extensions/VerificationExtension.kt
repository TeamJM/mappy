package info.journeymap.mappy.extensions

import com.gitlab.kordlib.core.behavior.channel.createMessage
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.rest.request.RestRequestException
import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.checks.inChannel
import com.kotlindiscord.kord.extensions.checks.notHasRole
import com.kotlindiscord.kord.extensions.checks.topRoleLower
import com.kotlindiscord.kord.extensions.extensions.Extension
import info.journeymap.mappy.config.config
import info.journeymap.mappy.defaultCheck
import info.journeymap.mappy.deleteIgnoringNotFound
import info.journeymap.mappy.deleteWithDelay
import info.journeymap.mappy.enums.Channels
import info.journeymap.mappy.enums.Roles
import kotlinx.coroutines.delay
import mu.KotlinLogging
import java.time.Instant

/** How long to wait before removing irrelevant messages - 10 seconds. **/
private const val DELETE_DELAY = 10_000L

/** How long to wait before retrying message removal on error - 2 seconds. **/
private const val RETRY_DELAY = 2_000L

/** How long to wait before applying the verification role - 5 seconds. **/
private const val ROLE_DELAY = 5_000L

/** Message sent to the user on verification. **/
private val VERIFICATION_MESSAGE = """
    Hello, and thanks for accepting our policies! For reference, here's what you just agreed to:

    **Code of Conduct:** <https://github.com/TeamJM/journeymap/blob/master/CONDUCT.md>
    **Rules:** <https://discordapp.com/channels/239040424214396929/239040424214396929/654442764041322497>

    :one: *Be respectful towards all users and staff members*
    :two: *Do not spam or post any NSFW content, and avoid sensitive subjects*
    :three: *Do not discriminate against or harass other users*
    :four: *Do not advertise your community or project unless staff has given you explicit permission to do so*
    :five: *Do not post any form of malicious content, and don't upload any mod JAR files*
    :six: *This is an English-speaking server - we require that everyone speaks English to the best of their ability*
    :seven: *Abide by Discord's Community Guidelines and Terms of Service*

    If you need to contact staff for any reason, feel free to send a message to <@!109040264529608704> and
    we'll reply as soon as we can.
""".trimIndent()

private val logger = KotlinLogging.logger {}

/**
 * New user verification extension.
 *
 * This extension provides a `!verify` command, as well as a [MessageCreateEvent] handler
 * which removes messages from the verification channel, letting the user know how they
 * can verify themselves.
 *
 * This extension is taken from Kotlin Discord's bot.
 */
class VerificationExtension(bot: ExtensibleBot) : Extension(bot) {
    override val name: String = "verification"

    override suspend fun setup() {
        val verifyCommand = command {
            name = "verify"
            aliases = arrayOf("accept", "verified", "accepted")
            hidden = true

            check(
                ::defaultCheck,
                inChannel(config.getChannel(Channels.CHECKPOINT)),
                notHasRole(config.getRole(Roles.VERIFIED)),
                topRoleLower(config.getRole(Roles.ADMIN))
            )

            action {
                message.deleteIgnoringNotFound()

                val author = message.getAuthorAsMember()!!
                val channel = config.getChannel(Channels.BOT_LOGS) as GuildMessageChannel

                channel.createMessage {
                    embed {
                        description = """
                            User ${author.mention} has verified themselves.
                        """.trimIndent()

                        timestamp = Instant.now()
                        title = "User verification"

                        field { inline = true; name = "U/N"; value = "`${author.username}`" }
                        field { inline = true; name = "Discrim"; value = "`${author.discriminator}`" }

                        if (author.nickname != null) {
                            field { inline = false; name = "Nickname"; value = "`${author.nickname}`" }
                        }

                        thumbnail { url = author.avatar.url }
                        footer { this.text = author.id.value }
                    }
                }

                try {
                    val dmChannel = author.getDmChannel()

                    dmChannel.createMessage(VERIFICATION_MESSAGE)
                    delay(ROLE_DELAY)
                    author.addRole(config.getRoleSnowflake(Roles.VERIFIED))
                } catch (e: RestRequestException) {
                    val sentMessage = message.channel.createMessage(
                        "${author.mention} $VERIFICATION_MESSAGE\n\n" +
                                "You'll be given access to the rest of the server shortly."
                    )

                    sentMessage.deleteWithDelay(DELETE_DELAY)
                    delay(ROLE_DELAY * 2)
                    author.addRole(config.getRoleSnowflake(Roles.VERIFIED))
                }
            }
        }

        val aliases = verifyCommand.aliases + verifyCommand.name

        event<MessageCreateEvent> {
            check(
                ::defaultCheck,
                inChannel(config.getChannel(Channels.CHECKPOINT)),
                notHasRole(config.getRole(Roles.VERIFIED)),
                topRoleLower(config.getRole(Roles.ADMIN))
            )

            action {
                with(it) {
                    val lowerMessage = message.content.toLowerCase()

                    aliases.forEach { alias ->
                        if (lowerMessage.startsWith("${bot.prefix}$alias")) {
                            return@action
                        }
                    }

                    message.channel.createMessage(
                        "${message.author!!.mention} Please send `!verify` to gain access to the rest of the server."
                    ).deleteWithDelay(DELETE_DELAY)

                    try {
                        message.deleteIgnoringNotFound()
                    } catch (e: RestRequestException) {
                        logger.warn(e) { "Failed to delete user's message, retrying in two seconds." }

                        delay(RETRY_DELAY)

                        try {
                            message.deleteIgnoringNotFound()
                        } catch (e: RestRequestException) {
                            logger.warn(e) { "Failed to delete user's message on the second attempt." }
                        }
                    }
                }
            }
        }
    }
}
