package info.journeymap.mappy.events

import net.dv8tion.jda.api.events.GenericEvent
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Listener(val value: KClass<out GenericEvent>)
