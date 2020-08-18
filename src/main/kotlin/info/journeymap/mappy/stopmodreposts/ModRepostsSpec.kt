package info.journeymap.mappy.stopmodreposts

import com.uchuhimo.konf.ConfigSpec

/** Config spec representing the data obtained from StopModReposts. **/
object ModRepostsSpec : ConfigSpec("") {  // There's no prefix for this one
    val sites by required<List<ModReposts>>(name = "sites")
}
