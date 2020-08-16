package info.journeymap.mappy.mod_reposts

data class ModReposts(
    val domain: String,
    val path: String,
    val pattern: String,
    val notes: String,

    val advertising: Int,
    val redistribution: Int,
    val miscellaneous: Int
)
