package info.journeymap.mappy

import com.charleskorn.kaml.Yaml
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.linkedin.urls.detection.UrlDetector
import com.linkedin.urls.detection.UrlDetectorOptions
import info.journeymap.mappy.config.Predefined

class UrlBlacklistHandler {
    val resourceUrl = "/predefined.yml".asResource()
    val predefinedConfig = Yaml.default.parse(Predefined.serializer(), resourceUrl!!.readText())
    val yaml = org.yaml.snakeyaml.Yaml()

    var urlCache: MutableMap<String, String> = mutableMapOf()

    fun checkUrls(message: String): List<Pair<String, String>> {
        val detector = UrlDetector(message, UrlDetectorOptions.Default)
        val domains = mutableListOf<String>()
        val problems = mutableListOf<Pair<String, String>>()

        for (url in detector.detect()) {
            if (!domains.contains(url.host) && this.urlCache.containsKey(url.host)) {
                problems.add(Pair(url.host, this.urlCache[url.host]!!))
                domains.add(url.host)
            }
        }

        return problems
    }

    suspend fun setup() {
        this.urlCache.clear()

        val urlBlacklist = this.predefinedConfig.urlBlacklist

        for (url in urlBlacklist.categoryAdult) {
            this.urlCache[url] = "Adult"
        }

        for (url in urlBlacklist.categoryAntiPrivacy) {
            this.urlCache[url] = "Anti-privacy"
        }

        for (url in urlBlacklist.categoryObjectionable) {
            this.urlCache[url] = "Objectionable"
        }

        val modRepostsData = Fuel.get(urlBlacklist.urls.stopModReposts).awaitStringResponseResult().third.get()
        val modRepostsUrls = this.yaml.load(modRepostsData) as List<Map<String, Any>>

        for (obj in modRepostsUrls) {
            this.urlCache[obj["domain"] as String] = "Mod repost site"
        }
    }
}

val urlBlacklistHandler = UrlBlacklistHandler()
