Mappy
=====

[![Mappy's Logo](https://raw.githubusercontent.com/TeamJM/Mappy/master/media/logo-512.jpg)](https://github.com/TeamJM/Mappy/tree/master/media)

Mappy is a Discord bot created for the [JourneyMap Discord server](https://discord.gg/eP8gE69). Mappy is written
fully in Kotlin, and handles some of the day to day moderation and utility tasks on the server.

Mappy is written using [kord-extensions](https://github.com/Kotlin-Discord/kord-extensions) and contains some code
written for the [Kotlin Discord bot](https://github.com/Kotlin-Discord/bot).

Contributing
============

We heavily recommend using IntelliJ IDEA when working with this project. Other IDEs are acceptable, but they will be
unable to understand the project files in this repo.

1. If you have commit access to the repo, create a branch for your changes
    * If not, fork it, but make sure you still create a branch for your changes

1. Clone the repo, switch to the branch, and get hacking
1. Use the `build` gradle task to compile Mappy - JARs will be placed in `build/libs` (the `-all` JAR is the one to run)
1. Open your pull request **early** and **mark it as a work-in-progress**
1. Once your changes are completed, provide screenshots and a summary of changes in your PR, and 
   **remove the work-in-progress status**
1. Wait for us to review your PR, and then address those reviews

Once your pull request has passed our review process, then it will be merged into `master`. Congratulations!

Usage
=====

We recommend that Mappy be run using Docker. That said, you can also directly run the production JAR - you can find
downloads attached to each Actions build.

Mappy makes use of the `konf` library, and in particular supports configuration files written in TOML. Configuration
values may also be provided using system properties or environment variables. The default configuration 
[can be found here](https://github.com/TeamJM/Mappy/blob/master/src/main/resources/default.toml). For example:

```toml
[bot]
guild = 239040424214396929
prefix = "!"
```

The `guild` entry under the `bot` header will be referred to as `bot.guild`, and 
[is defined here](https://github.com/TeamJM/Mappy/blob/master/src/main/kotlin/info/journeymap/mappy/config/spec/BotSpec.kt#L12).
When working with environment variables, you replace every dot (`.`) with an underscore (`_`), and make the entire key
upper-case. For example, `BOT_GUILD=239040424214396929`. For more information on how this works, see 
[part 3 of the konf quick-start](https://github.com/uchuhimo/konf#quick-start).

If you're wondering what can be configured, please 
[take a look at the config spec objects](https://github.com/TeamJM/Mappy/tree/master/src/main/kotlin/info/journeymap/mappy/config/spec).
