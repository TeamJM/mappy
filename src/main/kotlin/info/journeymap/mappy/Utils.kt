package info.journeymap.mappy

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member

private val channelMap: MutableMap<Long, GuildChannel?> = mutableMapOf()

fun Member.isStaff(): Boolean {
    for (role in this.roles) {
        if (role.idLong in config.roles.staffRoles) {
            return true
        }
    }

    return false
}

fun getNegativeEmbedBuilder(): EmbedBuilder {
    val builder = EmbedBuilder()

    builder.setColor(config.colours.red)
    builder.setTitle(config.embedTitles.negativeTitles.random())

    return builder
}

fun getPositiveEmbedBuilder(): EmbedBuilder {
    val builder = EmbedBuilder()

    builder.setColor(config.colours.green)
    builder.setTitle(config.embedTitles.positiveTitles.random())

    return builder
}
