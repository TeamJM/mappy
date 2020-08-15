package info.journeymap.mappy

import com.kotlindiscord.kord.extensions.ExtensibleBot
import info.journeymap.mappy.config.buildInfo
import info.journeymap.mappy.config.config
import io.sentry.Sentry
import mu.KotlinLogging

private val logger = KotlinLogging.logger("main")

/** The current instance of the bot. **/
val bot = ExtensibleBot(prefix = config.prefix, token = config.token)


suspend fun main() {
    val logger = KotlinLogging.logger {}

    logger.info { "Starting Mappy version ${buildInfo.version}." }

//    bot.addExtension(CleanExtension::class)
//    bot.addExtension(FilterExtension::class)
//    bot.addExtension(LoggingExtension::class)
//    bot.addExtension(ModerationExtension::class)
//    bot.addExtension(SubscriptionExtension::class)
//    bot.addExtension(SyncExtension::class)
//    bot.addExtension(VerificationExtension::class)
//
//    if (environment == "dev") {
//        bot.addExtension(TestExtension::class)
//    }

    bot.start()
}
