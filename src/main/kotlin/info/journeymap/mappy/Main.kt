package info.journeymap.mappy

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kordex.ext.mappings.extMappings
import com.kotlindiscord.kordex.ext.mappings.extMappingsCheck
import info.journeymap.mappy.config.buildInfo
import info.journeymap.mappy.config.config
import info.journeymap.mappy.extensions.FilterExtension
import info.journeymap.mappy.extensions.SubscriptionExtension
import mu.KotlinLogging

private val logger = KotlinLogging.logger("main")

@Suppress("MagicNumber", "UnderscoresInNumericLiterals")  // They're channel IDs
private val ALLOWED_CHANNELS = listOf(
    810665753871122436L,  // Dev bot spam
    629370152214855700L   // Test server bot commands
)

/** The current instance of the bot. **/
val bot = ExtensibleBot(prefix = config.prefix, token = config.token)

/** Let's do this, shall we? **/
suspend fun main() {
    val logger = KotlinLogging.logger {}

    logger.info { "Starting Mappy version ${buildInfo.version}." }

    bot.addExtension(::FilterExtension)
    bot.addExtension(::SubscriptionExtension)

    bot.extMappingsCheck { { event -> ALLOWED_CHANNELS.contains(event.message.channelId.value) } }
    bot.extMappings()

    bot.start {
        presence {
            this.playing("Type ${config.prefix}help")
        }
    }
}
