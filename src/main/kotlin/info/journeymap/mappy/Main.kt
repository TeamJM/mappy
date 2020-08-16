package info.journeymap.mappy

import com.kotlindiscord.kord.extensions.ExtensibleBot
import info.journeymap.mappy.config.buildInfo
import info.journeymap.mappy.config.config
import info.journeymap.mappy.extensions.FilterExtension
import info.journeymap.mappy.extensions.SubscribeExtension
import info.journeymap.mappy.extensions.VerificationExtension
import mu.KotlinLogging

private val logger = KotlinLogging.logger("main")

/** The current instance of the bot. **/
val bot = ExtensibleBot(prefix = config.prefix, token = config.token)


suspend fun main() {
    val logger = KotlinLogging.logger {}

    logger.info { "Starting Mappy version ${buildInfo.version}." }

    bot.addExtension(FilterExtension::class)
    bot.addExtension(SubscribeExtension::class)
    bot.addExtension(VerificationExtension::class)

    bot.start()
}
