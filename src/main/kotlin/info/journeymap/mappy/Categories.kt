package info.journeymap.mappy

import com.jagrosh.jdautilities.command.Command

enum class Categories(val category: Command.Category) {
    //    Moderation(Command.Category("Moderation")),
    User(Command.Category("User")),
    Verification(Command.Category("Verification"))
}