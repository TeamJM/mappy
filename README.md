Mappy
=====

[![Mappy's Logo](https://raw.githubusercontent.com/TeamJM/Mappy/master/media/logo-512.jpg)](https://github.com/TeamJM/Mappy/tree/master/media)

Mappy is a Discord bot created for the [JourneyMap Discord server](https://discord.gg/eP8gE69). Mappy is written
fully in Kotlin, and handles some of the day to day moderation and utility tasks on the server.

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

The first time you run Mappy, it will generate a configuration file - `config.yml`. Open this up and fill it out. If
you prefer, you can copy `config.yml.example` to `config.yml` and fill that out instead - it's commented to make your
life a little easier.

If you prefer, Mappy can use environment variables when generating this initial config file. For more information on
which environment variables are mapped to which configuration values, please see 
[Config.kt](https://github.com/TeamJM/Mappy/blob/master/src/main/kotlin/info/journeymap/mappy/Config.kt).

You can prevent Mappy from generating a config file (so it just uses the environment variables directly) by setting
the `NO_CONFIG` environment variable.
