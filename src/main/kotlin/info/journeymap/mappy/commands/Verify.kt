package info.journeymap.mappy.commands

import com.jagrosh.jdautilities.command.CommandEvent
import info.journeymap.mappy.Categories
import info.journeymap.mappy.config
import info.journeymap.mappy.isStaff
import kotlinx.coroutines.future.await
import mu.KotlinLogging
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.PrivateChannel
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import java.awt.Color

private val logger = KotlinLogging.logger("command/verify")
private val privateMessage: String = "Thanks for accepting our rules!" +
        "\n\n" +
        "For your own records, please note that you can always find the latest version of our ruleset in " +
        "<#${config.channels.info}>, and the latest version of our Code of Conduct can be found " +
        "[on GitHub](https://github.com/TeamJM/journeymap/blob/master/CONDUCT.md)." +
        "\n\n" +
        "Please note that our rules and Code of Conduct may be modified in the future. Additionally, please " +
        "feel free to contact us if you have any questions or concerns."

class Verify : AsyncCommand() {
    init {
        this.category = Categories.Verification.category
        this.help = "Verify that you accept our rules"
        this.name = "verify"
        this.botPermissions = arrayOf(Permission.MANAGE_ROLES, Permission.MESSAGE_MANAGE)
    }

    override suspend fun command(event: CommandEvent) {
        if (event.channel.idLong != config.channels.checkpoint && event.member?.isStaff() != true) {
            // Do nothing if we're not in the checkpoint channel
            logger.debug { "Command sent to incorrect channel" }
            return
        }

        val role: Role? = event.jda.getRoleById(config.roles.verified)

        if (role == null) {
            logger.warn { "Configured verification role not found." }
            return
        }

        if (event.member.roles.contains(role)) {
            // Do nothing if the user is already verified (staff?)
            logger.debug { "User is already verified" }
            return
        }

        try {
            logger.debug { "Adding verified role to user" }
            event.guild.addRoleToMember(event.member, role).submit().await()
        } catch (t: Throwable) {
            logger.error(t) { "Failed to add role" }
            event.channel.sendMessage(
                "I encountered a problem while trying to verify you - please notify a member of staff."
            ).submit().await()
            return
        }

        try {
            event.message.delete().submit().await()
        } catch (t: Throwable) {
            logger.warn(t) { "Failed to delete verification command message" }
        }

        try {
            val logChannel: TextChannel? = event.jda.getTextChannelById(config.channels.botLogs)

            if (logChannel != null) {
                val embed = EmbedBuilder()

                embed.setTitle("User verified")
                embed.setThumbnail(event.author.avatarUrl)
                embed.setImage(event.author.avatarUrl)
                embed.setDescription("${event.author.asMention}\n${event.author.name}#${event.author.discriminator}")
                embed.setFooter("ID: ${event.author.idLong}")
                embed.setTimestamp(event.message.timeCreated)

                logChannel.sendMessage(embed.build()).submit().await()
            } else {
                logger.warn { "Logging channel not found" }
            }
        } catch (t: Throwable) {
            logger.error(t) { "Failed to log verification in logging channel" }
        }

        try {
            val privateChannel: PrivateChannel = event.author.openPrivateChannel().submit().await()
            val embed = EmbedBuilder()

            embed.setDescription(privateMessage)
            embed.setColor(Color(config.colours.green))

            privateChannel.sendMessage(embed.build()).submit().await()
        } catch (t: Throwable) {
            logger.warn(t) { "Failed to DM user after verification" }
        }
    }
}
