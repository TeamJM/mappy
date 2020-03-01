package info.journeymap.mappy.config

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UrlBlacklist(
    @Required
    @SerialName("urls")
    val urls: UrlBlacklistUrls = UrlBlacklistUrls(),

    @Required
    @SerialName("adult")
    val categoryAdult: List<String> = listOf(),

    @Required
    @SerialName("anti_privacy")
    val categoryAntiPrivacy: List<String> = listOf(),

    @Required
    @SerialName("objectionable")
    val categoryObjectionable: List<String> = listOf()
)

@Serializable
data class UrlBlacklistUrls(
    @Required
    @SerialName("stop_mod_reposts")
    val stopModReposts: String = ""
)

@Serializable
data class Predefined(
    @SerialName("url_blacklist")
    val urlBlacklist: UrlBlacklist = UrlBlacklist()
)