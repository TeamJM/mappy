package info.journeymap.mappy.listeners

import info.journeymap.mappy.config
import info.journeymap.mappy.events.Listener
import info.journeymap.mappy.isStaff
import info.journeymap.mappy.urlBlacklistHandler
import kotlinx.coroutines.future.await
import kotlinx.coroutines.time.delay
import mu.KotlinLogging
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.time.Duration


private val logger = KotlinLogging.logger("listener/message")

class MessageListener {
    @Listener(MessageReceivedEvent::class)
    suspend fun verificationNagMessage(event: MessageReceivedEvent) {
        if (event.author.idLong == event.jda.selfUser.idLong) {
            return  // Don't action our own messages!
        }

        if (!event.isFromGuild || event.guild.idLong != config.serverId) {
            return  // Only actions messages from our configured server
        }

        if (event.channel.idLong == config.channels.checkpoint && !event.message.contentRaw.startsWith("!verify")) {
            if (event.member!!.isStaff()) {
                return
            }

            event.message.delete().submit().await()

            val message =
                event.channel.sendMessage("${event.author.asMention} Please use `!verify` to verify that you accept our rules.")
                    .submit().await()
            delay(Duration.ofSeconds(10))
            message.delete().submit().await()
        }
    }
}
