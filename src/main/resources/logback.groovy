import ch.qos.logback.core.joran.spi.ConsoleTarget

def debug = System.getenv().containsKey("DEBUG")

def defaultLevel = DEBUG

if (!debug) {
    defaultLevel = INFO
} else {
    // Silence warning about missing native PRNG
    logger("io.ktor.util.random", ERROR)

    // Silence some DEBUG messages from Sentry
    logger("io.sentry.DefaultSentryClientFactory", INFO)
    logger("io.sentry.DefaultSentryClientFactory", INFO)
    logger("io.sentry.SentryClient", INFO)
    logger("io.sentry.SentryClientFactory", INFO)
    logger("io.sentry.config.FileResourceLoader", INFO)
    logger("io.sentry.config.provider.EnvironmentConfigurationProvider", INFO)
    logger("io.sentry.config.provider.ResourceLoaderConfigurationProvider", INFO)
}

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss:SSS Z} | %5level | %40.40logger{40} | %msg%n"
    }

    target = ConsoleTarget.SystemErr
}

root(defaultLevel, ["CONSOLE"])
