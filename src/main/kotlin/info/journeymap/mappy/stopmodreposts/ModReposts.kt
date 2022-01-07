package info.journeymap.mappy.stopmodreposts

/** Data class representing an entry in the StopModReposts data. **/
@Suppress("UndocumentedPublicProperty")  // I mean, come on.
data class ModReposts(
    val domain: String,
    val path: String,
    val notes: String,
    val reason: String,
)
