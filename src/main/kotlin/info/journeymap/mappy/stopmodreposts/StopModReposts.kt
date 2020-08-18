package info.journeymap.mappy.stopmodreposts

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml

private const val REPOSTS_URL = "https://api.varden.info/smr/sitelist.php?format=yaml"

/** Config-like class providing access to StopModReposts data. **/
class StopModReposts {
    private val config = Config {
        addSpec(ModRepostsSpec)
    }
        .from.yaml.url(REPOSTS_URL)

    /** Map of bad domains from StopModReposts. **/
    val domains: Map<String, ModReposts> = config[ModRepostsSpec.sites].map { it.domain to it }.toMap()
}

/**
 * The currently loaded [StopModReposts] object.
 *
 * You should always use this instead of constructing an instance of [StopModReposts] yourself.
 */
val modReposts = StopModReposts()
