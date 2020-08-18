package info.journeymap.mappy.config.spec

import com.uchuhimo.konf.ConfigSpec

private val adultDefaults = listOf(  // Inappropriate/adult sites
    "pornhub.com",
    "redtube.com",
    "spankwire.com",
    "xvideos.com",
    "xhampster.com",
    "youporn.com"
)

private val antiPrivacyDefaults = listOf(  // Sites that exist to invade your privacy
    // Invasive server data exfil bot sites
    "dis.cool",
    "tracr.co",

    // Main Grabify domain
    "grabify.link",

    // Known Grabify shortener domains
    "bmwforum.co",
    "leancoding.co",
    "spottyfly.com",
    "stopify.co",
    "yoütu.be",
    "discörd.com",
    "minecräft.com",
    "freegiftcards.co",
    "disçordapp.com",
    "fortnight.space",
    "fortnitechat.site",
    "joinmy.site",
    "curiouscat.club",
    "catsnthings.`fun`",
    "yourtube.site",
    "youtubeshort.watch",
    "catsnthing.com",
    "youtubeshort.pro",
    "canadianlumberjacks.online",
    "poweredbydialup.club",
    "poweredbydialup.online",
    "poweredbysecurity.org",
    "poweredbysecurity.online"
)

private val objectionableDefaults = listOf(  // Other objectionable content
    "liveleak.com",
    "motherless.com"
)

/**
 * A class representing the `filter` section of the configuration.
 *
 * This is used by Konf, and will not need to be accessed externally.
 */
object FilterSpec : ConfigSpec("filter") {
    /** Whether to make use of StopModReposts data. **/
    val stopModReposts by optional(default = true)

    /**
     * A class representing the `filter.domains` section of the configuration.
     *
     * This is used by Konf, and will not need to be accessed externally.
     */
    object DomainsSpec : ConfigSpec("domains") {
        /** Banned adult domains. **/
        val adult by optional(default = adultDefaults)

        /** Banned anti-privacy domains. **/
        val antiPrivacy by optional(default = antiPrivacyDefaults)

        /** Banned objectionable domains. **/
        val objectionable by optional(default = objectionableDefaults)
    }
}
