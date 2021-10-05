package info.journeymap.mappy

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.modules.extra.mappings.extMappings
import info.journeymap.mappy.config.buildInfo
import info.journeymap.mappy.config.config
import info.journeymap.mappy.extensions.FilterExtension
import info.journeymap.mappy.extensions.PhishingExtension
import info.journeymap.mappy.extensions.SubscriptionExtension
import mu.KotlinLogging

@Suppress("MagicNumber", "UnderscoresInNumericLiterals")  // They're channel IDs
private val ALLOWED_CHANNELS = listOf(
    810665753871122436L,  // Dev bot spam
    629370152214855700L   // Test server bot commands
)

/** Let's do this, shall we? **/
suspend fun main() {
    val logger = KotlinLogging.logger {}

    logger.info { "Starting Mappy, version ${buildInfo.version}." }

    val bot = ExtensibleBot(config.token) {
        chatCommands {
            enabled = true

            defaultPrefix = config.prefix
        }

        extensions {
            add(::FilterExtension)
            add(::PhishingExtension)
            add(::SubscriptionExtension)

            extMappings {
                commandCheck { _ ->
                    {
                        if (ALLOWED_CHANNELS.contains(event.message.channelId.value)) {
                            pass()
                        } else {
                            fail("Must be in one of: " + ALLOWED_CHANNELS.joinToString(", ") { "<#$it}>" })
                        }
                    }
                }
            }
        }

        presence {
            playing("Type ${config.prefix}help")
        }
    }

    bot.start()
}
