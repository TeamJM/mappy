package info.journeymap.mappy.extensions

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.extensions.Extension

class VerificationExtension(bot: ExtensibleBot) : Extension(bot) {
    override val name: String = "verification"

    override suspend fun setup() {
        TODO("Not yet implemented")
    }
}
