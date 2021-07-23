package info.journeymap.mappy

import com.kotlindiscord.kord.extensions.checks.*
import com.kotlindiscord.kord.extensions.checks.types.CheckContext
import dev.kord.core.event.Event
import info.journeymap.mappy.config.config
import info.journeymap.mappy.enums.Channels
import info.journeymap.mappy.enums.Roles
import mu.KotlinLogging

/**
 * Default check we do for almost every event and command, message creation flavour.
 *
 * Ensures:
 * * That the message was sent to the configured primary guild
 * * That we didn't send the message
 * * That another bot didn't send the message
 *
 * @param event The event to run this check against.
 */
val defaultCheck: suspend CheckContext<Event>.() -> Unit = {
    val logger = KotlinLogging.logger {}

    val message = messageFor(event)?.asMessage()

    when {
        message == null                                      -> {
            logger.debug { "Failing check: Message for event $event is null. This type of event may not be supported." }

            fail()
        }

        message.getGuildOrNull()?.id != config.getGuild().id -> {
            logger.debug { "Failing check: Not in the correct guild" }

            fail("Must be in the JourneyMap server")
        }

        message.author == null                               -> {
            logger.debug { "Failing check: Message sent by a webhook or system message" }

            fail()
        }

        message.author!!.id == event.kord.getSelf().id         -> {
            logger.debug { "Failing check: We sent this message" }

            fail()
        }

        message.author!!.isBot == true                       -> {
            logger.debug { "Failing check: This message was sent by another bot" }

            fail()
        }

        else                                                 -> {
            logger.debug { "Passing check" }

            pass()
        }
    }
}

/**
 * Check to ensure an event happened within the bot commands channel.
 *
 * @param event The event to run this check against.
 */
val inBotChannel: suspend CheckContext<Event>.() -> Unit = {
    val logger = KotlinLogging.logger {}

    val channel = channelFor(event)

    when {
        channel == null                                           -> {
            logger.debug { "Failing check: Channel is null" }

            fail()
        }

        channel.id != config.getChannel(Channels.BOT_COMMANDS).id -> {
            logger.debug { "Failing check: Not in bot commands" }

            fail("Must be in " + config.getChannel(Channels.BOT_COMMANDS).mention)
        }

        else                                                      -> {
            logger.debug { "Passing check" }

            pass()
        }
    }
}

/**
 * Check that checks that the user is at least a moderator, or that the event
 * happened in the bot commands channel.
 */
val botChannelOrModerator = or(
    inBotChannel,
    hasRole { config.getRole(Roles.MODERATOR) },
    hasRole { config.getRole(Roles.ADMIN) },
    hasRole { config.getRole(Roles.OWNER) }
)

/**
 * Check that ensures an event wasn't fired by a bot. If an event doesn't
 * concern a specific user, then this check will pass.
 */
val isNotBot: suspend CheckContext<Event>.() -> Unit = {
    val logger = KotlinLogging.logger {}

    val user = userFor(event)

    when {
        user == null                -> {
            logger.debug { "Passing check: User for event $event is null." }

            pass()
        }

        user.asUser().isBot == true -> {
            logger.debug { "Failing check: User $user is a bot." }

            fail()
        }

        else                        -> {
            logger.debug { "Passing check." }

            pass()
        }
    }
}
