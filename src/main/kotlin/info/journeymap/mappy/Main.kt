package info.journeymap.mappy

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.modules.extra.mappings.extMappings
import com.kotlindiscord.kord.extensions.modules.extra.phishing.DetectionAction
import com.kotlindiscord.kord.extensions.modules.extra.phishing.extPhishing
import dev.kord.common.entity.Snowflake
import info.journeymap.mappy.config.buildInfo
import info.journeymap.mappy.config.config
import info.journeymap.mappy.extensions.FilterExtension
import info.journeymap.mappy.extensions.SubscriptionExtension
import mu.KotlinLogging

@Suppress("MagicNumber", "UnderscoresInNumericLiterals")  // They're channel IDs
private val ALLOWED_CHANNELS = listOf(
    Snowflake(810665753871122436),  // Dev bot spam
    Snowflake(629370152214855700)   // Test server bot commands
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
            add(::SubscriptionExtension)

            extMappings {
                commandCheck { _ ->
                    {
                        if (ALLOWED_CHANNELS.contains(event.interaction.channelId)) {
                            pass()
                        } else {
                            fail("Must be in one of: " + ALLOWED_CHANNELS.joinToString(", ") { "<#$it}>" })
                        }
                    }
                }
            }

            extPhishing {
                appName = "JourneyMap, Mappy Bot"
                detectionAction = DetectionAction.Ban
                logChannelName = "mappy-logs"
            }
        }

        presence {
            playing("Type ${config.prefix}help")
        }
    }

    bot.start()
}
