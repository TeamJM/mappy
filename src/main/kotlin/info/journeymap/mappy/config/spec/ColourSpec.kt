package info.journeymap.mappy.config.spec

import com.uchuhimo.konf.ConfigSpec

/**
 * A class representing the `colours` section of the configuration.
 *
 * This is used by Konf, and will not need to be accessed externally.
 */
object ColourSpec : ConfigSpec() {
    /** Configured green colour. **/
    val green by optional(default = 0x169718)

    /** Configured red colour. **/
    val red by optional(default = 0x961917)
}
