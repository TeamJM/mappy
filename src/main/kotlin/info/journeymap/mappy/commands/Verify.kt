package info.journeymap.mappy.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import info.journeymap.mappy.Categories
import info.journeymap.mappy.config
import mu.KotlinLogging
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Role

private val logger = KotlinLogging.logger("command/verify")

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
        event.guild.addRoleToMember(event.member, role).complete()
    }
}
