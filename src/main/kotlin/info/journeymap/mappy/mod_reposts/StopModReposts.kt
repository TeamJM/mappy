package info.journeymap.mappy.mod_reposts

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml

private const val repostsUrl = "https://api.varden.info/smr/sitelist.php?format=yaml"

class StopModReposts {
    private val config = Config {
        addSpec(ModRepostsSpec)
    }
        .from.yaml.url(repostsUrl)

    val domains: Map<String, ModReposts> = config[ModRepostsSpec.sites].map { it.domain to it }.toMap()
}

val modReposts = StopModReposts()
