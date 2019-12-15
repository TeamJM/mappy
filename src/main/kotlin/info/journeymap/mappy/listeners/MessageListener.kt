package info.journeymap.mappy.listeners

import info.journeymap.mappy.config
import info.journeymap.mappy.events.Listener
import info.journeymap.mappy.isStaff
import kotlinx.coroutines.future.await
import kotlinx.coroutines.time.delay
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.time.Duration

class MessageListener {
    @Listener(MessageReceivedEvent::class)
    suspend fun onMessage(event: MessageReceivedEvent) {
        if (event.author.idLong == event.jda.selfUser.idLong) {
            return  // Don't action our own messages!
        }

        if (event.guild.idLong != config.serverId || !event.isFromGuild) {
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