package info.journeymap.mappy.extensions

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.extensions.Extension

class FilterExtension(bot: ExtensibleBot) : Extension(bot) {
    override val name: String = "stop-mod-reposts"

    override suspend fun setup() {
        TODO("Not yet implemented")
    }
}
