package info.journeymap.mappy.stopmodreposts

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.toValue

private const val REPOSTS_URL = "https://api.stopmodreposts.org/sites.yaml"

/** Config-like class providing access to StopModReposts data. **/
class StopModReposts {
    private val config = Config()
        .from.yaml.url(REPOSTS_URL)

    /** Map of bad domains from StopModReposts. **/
    val domains: Map<String, ModReposts> = config.toValue<List<ModReposts>>().associateBy { it.domain }
}

/**
 * The currently loaded [StopModReposts] object.
 *
 * You should always use this instead of constructing an instance of [StopModReposts] yourself.
 */
val modReposts = StopModReposts()
