package info.journeymap.mappy.config.spec

import com.uchuhimo.konf.ConfigSpec

/**
 * A class representing the `roles` section of the configuration.
 *
 * This is used by Konf, and will not need to be accessed externally.
 */
object RolesSpec : ConfigSpec("roles") {
    /** Configured owner role ID. **/
    val owner by required<Long>()

    /** Configured admin role ID. **/
    val admin by required<Long>()

    /** Configured moderator role ID. **/
    val moderator by required<Long>()

    /** Configured bot role ID. **/
    val bot by required<Long>()

    /** Configured announcements role ID. **/
    val announcements by required<Long>()

    /** Configured muted role ID. **/
    val muted by required<Long>()

    /** Configured verified user role ID. **/
    val verified by required<Long>()
}
