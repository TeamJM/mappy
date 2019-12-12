package info.journeymap.mappy.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class AsyncCommand : Command() {
    abstract suspend fun command(event: CommandEvent)

    override fun execute(event: CommandEvent) {
        GlobalScope.launch {
            command(event)
        }
    }
}
