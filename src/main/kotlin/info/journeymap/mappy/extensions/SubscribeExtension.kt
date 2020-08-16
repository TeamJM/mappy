package info.journeymap.mappy.extensions

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.extensions.Extension

class SubscribeExtension(bot: ExtensibleBot) : Extension(bot) {
    override val name: String = "subscribe"

    override suspend fun setup() {
        TODO("Not yet implemented")
    }
}
