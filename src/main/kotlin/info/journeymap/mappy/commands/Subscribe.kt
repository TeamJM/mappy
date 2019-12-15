package info.journeymap.mappy.commands

import com.jagrosh.jdautilities.command.CommandEvent
import info.journeymap.mappy.Categories
import info.journeymap.mappy.config
import info.journeymap.mappy.getNegativeEmbedBuilder
import info.journeymap.mappy.isStaff
import kotlinx.coroutines.future.await
import kotlinx.coroutines.time.delay
import mu.KotlinLogging
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Role
import java.time.Duration

private val logger = KotlinLogging.logger("command/subscribe")

class Subscribe : AsyncCommand() {
    init {
        this.category = Categories.User.category
        this.help = "Subscribe to announcements"
        this.name = "subscribe"
        this.botPermissions = arrayOf(Permission.MANAGE_ROLES)
    }

    override suspend fun command(event: CommandEvent) {
        if (event.channel.idLong != config.channels.botCommands && event.member?.isStaff() != true) {
            // Warn user if we're not in the correct channel
            val embed = getNegativeEmbedBuilder()
            val channel = event.jda.getGuildChannelById(config.channels.botCommands)

            if (channel == null) {
                embed.setDescription("This command may only be used in the bot commands channel.")
            } else {
                embed.setDescription("This command may only be used in <#${channel.id}>.")
            }

            try {
                val message = event.channel.sendMessage(embed.build()).submit().await()

                delay(Duration.ofSeconds(10))

                message.delete().submit().await()
                event.message.delete().submit().await()
            } catch (t: Throwable) {
                logger.error(t) { "Error while asking user to move to the bot commands channel" }
            }

            return
        }

        val role: Role? = event.jda.getRoleById(config.roles.announcements)

        if (role == null) {
            event.channel.sendMessage("${event.author.asMention} Announcements role not found. Please notify a member of staff!")
                .submit().await()
            return
        }

        if (event.member.roles.contains(role)) {
            // Do nothing if the user is already subscribed
            event.channel.sendMessage("${event.author.asMention} You're already subscribed!").submit().await()
            return
        }

        try {
            logger.debug { "Adding announcements role to user" }
            event.guild.addRoleToMember(event.member, role).submit().await()
            event.channel.sendMessage(
                "${event.author.asMention} You have subscribed to our announcements! Please note: you can use " +
                        "`!unsubscribe` at any time, if you decide you no longer wish to receive announcement notifications."
            ).submit().await()
        } catch (t: Throwable) {
            logger.error(t) { "Failed to add role" }
            event.channel.sendMessage(
                "${event.author.asMention} An error occurred while subscribing you. Please notify a member of staff!"
            ).submit().await()

            return
        }
    }
}
