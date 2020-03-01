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

    @Listener(MessageReceivedEvent::class)
    suspend fun handleUrlBlacklist(event: MessageReceivedEvent) {
        if (event.author.idLong == event.jda.selfUser.idLong) {
            return  // Don't action our own messages!
        }

        if (!event.isFromGuild || event.guild.idLong != config.serverId) {
            return  // Only actions messages from our configured server
        }

        if (event.member!!.isStaff()) {
            return  // Don't action staff messages
        }

        val problems = urlBlacklistHandler.checkUrls(event.message.contentDisplay)

        if (problems.isNotEmpty()) {
            event.message.delete().submit().await()

            try {
                val modsChannel: TextChannel? = event.jda.getTextChannelById(config.channels.mods)

                if (modsChannel != null) {
                    val embed = EmbedBuilder()

                    embed.setTitle("Blacklisted domain detected")
                    embed.setDescription(
                        "**Author:** ${event.author.asMention} (${event.author.name}#${event.author.discriminator})\n\n" +
                                problems.sortedBy { it.first }.joinToString("\n") { "`${it.first}`: ${it.second}" } +
                                "\n\n" +
                                ">>> ${event.message.contentRaw}"
                    )
                    embed.setFooter("ID: ${event.author.idLong}")
                    embed.setTimestamp(event.message.timeCreated)

                    modsChannel.sendMessage(embed.build()).submit().await()
                } else {
                    logger.warn { "Logging channel not found" }
                }
            } catch (t: Throwable) {
                logger.error(t) { "Failed to log verification in logging channel" }
            }

            val response = event.message.channel.sendMessage(
                "${event.author.asMention} We've detected a blacklisted URL in your message, so it's been " +
                        "removed. If you feel that this was in error, please contact a member of staff."
            ).submit().await()

            delay(Duration.ofSeconds(10))
            response.delete().submit().await()
        }
    }
}