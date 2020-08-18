package info.journeymap.mappy.config.spec

import com.uchuhimo.konf.ConfigSpec

private val defaultPositiveTitles: List<String> = listOf(
    "Affirmative",
    "As the prophecy foretold",
    "Caaaaan do",
    "Eh, might as well",
    "Gotcha",
    "I don't see why not",
    "No problem",
    "Okay, but only because I like you",
    "Right on",
    "Sounds good to me"
)

private val defaultNegativeTitles: List<String> = listOf(
    "Absolutely not",
    "How about no",
    "I don't have time for this",
    "I'm sorry, Dave",
    "Nope, not gonna happen",
    "Not even once",
    "Not in this reality",
    "Not this time, friendo",
    "There's a time and place for everything",
    "You're not my mom"
)

/**
 * A class representing the `embed` section of the configuration.
 *
 * This is used by Konf, and will not need to be accessed externally.
 */
object EmbedSpec : ConfigSpec("embeds") {
    /** Configured positive embed titles. **/
    val positiveTitles by optional(default = defaultPositiveTitles)

    /** Configured negative embed titles. **/
    val negativeTitles by optional(default = defaultNegativeTitles)
}
