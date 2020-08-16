package info.journeymap.mappy.mod_reposts

import com.uchuhimo.konf.ConfigSpec

object ModRepostsSpec : ConfigSpec() {
    val sites by required<List<ModReposts>>()
}
