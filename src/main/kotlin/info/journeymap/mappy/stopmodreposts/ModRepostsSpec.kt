package info.journeymap.mappy.stopmodreposts

import com.uchuhimo.konf.ConfigSpec

/** Config spec representing the data obtained from StopModReposts. **/
object ModRepostsSpec : ConfigSpec() {
    val sites by required<List<ModReposts>>()
}
