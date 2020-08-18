package info.journeymap.mappy.enums

/**
 * An enum representing each type of embed title that may be configured, for easier reference.
 *
 * @param value A human-readable representation of the given title type.
 */
enum class Titles(val value: String) {
    /** Positive titles. **/
    POSITIVE("positive"),

    /** Negative titles. **/
    NEGATIVE("negative")
}
