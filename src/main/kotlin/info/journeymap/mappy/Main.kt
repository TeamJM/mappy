package info.journeymap.mappy

import com.charleskorn.kaml.Yaml
import com.jagrosh.jdautilities.command.CommandClientBuilder
import info.journeymap.mappy.commands.Verify
import mu.KotlinLogging
import net.dv8tion.jda.api.JDABuilder
import java.io.File
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger("main")
var config: Config = Config()


fun main(args: Array<String>): Unit {
    val configFile = File("config.yml")

    var loadedConfig: Config? = null

    if (System.getenv("NO_CONFIG") != null) {
        loadedConfig = Config()
    } else if (loadedConfig == null && !configFile.exists()) {
        loadedConfig = Config()

        try {
            configFile.createNewFile()
            configFile.writeText(Yaml.default.stringify(Config.serializer(), loadedConfig))
            logger.info { "Created config file 'config.yml' - please fill it out and restart the bot." }

            exitProcess(0)
        } catch (e: Throwable) {
            logger.error(e) { "Failed to create config file." }

            exitProcess(1)
        }
    } else {
        try {
            loadedConfig = Yaml.default.parse(Config.serializer(), configFile.readText())
        } catch (e: Throwable) {
            logger.error(e) { "Failed to load config file." }

            exitProcess(1)
        }
    }

    config = loadedConfig

    val clientBuilder = CommandClientBuilder()
    val client = clientBuilder.setPrefix("!")
        .setOwnerId(config.ownerId.toString())
        .addCommand(Verify())
        .build()

    val builder = JDABuilder(config.token)
        .addEventListeners(client)

    val bot = builder.build()

    bot.awaitReady()
}
