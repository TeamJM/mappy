package info.journeymap.mappy.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import dev.kord.core.any
import info.journeymap.mappy.botChannelOrModerator
import info.journeymap.mappy.config.config
import info.journeymap.mappy.defaultCheck
import info.journeymap.mappy.deleteIgnoringNotFound
import info.journeymap.mappy.deleteWithDelay
import info.journeymap.mappy.enums.Roles
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/** How long to wait before removing response messages - 10 seconds. **/
private const val DELETE_DELAY = 10_000L

/**
 * Role-based subscriptions extension.
 *
 * This extension provides a `!subscribe` and `!unsubscribe` command, to allow
 * users to give themselves the announcements role and remove it later if they wish.
 *
 * This extension is taken from Kotlin Discord's bot.
 */
class SubscriptionExtension : Extension() {
    override val name: String = "subscription"

    override suspend fun setup() {
        chatCommand {
            name = "subscribe"
            aliases = arrayOf("sub")

            description = """
                Subscribe to important announcements channel notifications.
                
                This command will give you the Announcements role, which we mention
                from time to time when we have important announcements to make.
                
                Use `!unsubscribe` to remove the role at any time.
            """.trimIndent()

            check(
                defaultCheck,
                botChannelOrModerator
            )

            action {
                message.deleteIgnoringNotFound()

                val author = message.getAuthorAsMember()!!
                val role = config.getRoleSnowflake(Roles.ANNOUNCEMENTS)

                if (author.roles.any { it.id == role }) {
                    message.channel.createMessage(
                        "${author.mention} You're already subscribed to our announcements!"
                    ).deleteWithDelay(DELETE_DELAY)
                } else {
                    author.addRole(role)

                    message.channel.createMessage(
                        "${author.mention} Successfully subscribed to our announcements."
                    ).deleteWithDelay(DELETE_DELAY)
                }
            }
        }

        chatCommand {
            name = "unsubscribe"
            aliases = arrayOf("unsub")

            description = """
                Unsubscribe from important announcements channel notifications.
                
                This command will remove your Announcements role, which we mention
                from time to time when we have important announcements to make.
                
                Use `!subscribe` to grant yourself the role at any time.
            """.trimIndent()

            check(
                defaultCheck,
                botChannelOrModerator
            )

            action {
                message.deleteIgnoringNotFound()

                val author = message.getAuthorAsMember()!!
                val role = config.getRoleSnowflake(Roles.ANNOUNCEMENTS)

                if (!author.roles.any { it.id == role }) {
                    message.channel.createMessage(
                        "${author.mention} You're not subscribed to our announcements!"
                    ).deleteWithDelay(DELETE_DELAY)
                } else {
                    author.removeRole(role)

                    message.channel.createMessage(
                        "${author.mention} Successfully unsubscribed from our announcements."
                    ).deleteWithDelay(DELETE_DELAY)
                }
            }
        }
    }
}
