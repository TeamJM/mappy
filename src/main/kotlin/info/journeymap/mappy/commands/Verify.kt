package info.journeymap.mappy.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import info.journeymap.mappy.Categories
import info.journeymap.mappy.config
import mu.KotlinLogging
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Role
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

class Verify : Command() {
    init {
        this.category = Categories.Verification.category
        this.help = "Verify that you accept our rules"
        this.name = "verify"
        this.botPermissions = arrayOf(Permission.MANAGE_ROLES)
    }

    override fun execute(event: CommandEvent) {
        if (event.channel.idLong != config.channels.checkpoint) {
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

        logger.debug { "Adding verified role to user" }

        val embed = EmbedBuilder()

        embed.setDescription(privateMessage)
        embed.setColor(Color(config.colours.journeyMapGreen))

        event.guild.addRoleToMember(event.member, role).queue(
            {
                event.author.openPrivateChannel().queue {
                    it.sendMessage(embed.build()).queue(
                        { event.message.delete().queue() },
                        {}
                    )
                }
            },
            {
                logger.error(it) { "Failed to add role: $it" }
                event.channel.sendMessage("I encountered a problem while trying to verify you - please notify a member of staff.")
                    .queue(
                        {},
                        { error -> logger.error(error) { "Failed to send message: $error" } }
                    )
            }
        )
    }
}
