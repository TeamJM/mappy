package info.journeymap.mappy.config.spec

import com.uchuhimo.konf.ConfigSpec

/**
 * A class representing the `channels` section of the configuration.
 *
 * This is used by Konf, and will not need to be accessed externally.
 */
object ChannelsSpec : ConfigSpec("channels") {
    /** Configured bot-commands channel ID. **/
    val botCommands by required<Long>()

    /** Configured bot-logs channel ID. **/
    val botLogs by required<Long>()

    /** Configured mods channel ID. **/
    val mods by required<Long>()

    /** Configured info-log channel ID. **/
    val info by required<Long>()

    /** Configured checkpoint channel ID. **/
    val checkpoint by required<Long>()

    /** List of staff channels. **/
    val staffChannels by required<List<Long>>()
}
