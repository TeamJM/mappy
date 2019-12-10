package info.journeymap.mappy

import com.charleskorn.kaml.Yaml
import mu.KotlinLogging
import net.dv8tion.jda.api.JDABuilder
import java.io.File
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger("main")


fun main(args: Array<String>): Unit {
    val configFile = File("config.yml")
    var config: Config

    if (!configFile.exists()) {
        try {
            config = Config()
            configFile.createNewFile()
            configFile.writeText(Yaml.default.stringify(Config.serializer(), config))
            logger.info { "Created config file 'config.yml' - please fill it out and restart the bot." }

            exitProcess(0)
        } catch (e: Throwable) {
            logger.error(e) { "Failed to create config file." }

            exitProcess(1)
        }
    }

    config = Yaml.default.parse(Config.serializer(), configFile.readText())

    val builder = JDABuilder(config.token)
}
