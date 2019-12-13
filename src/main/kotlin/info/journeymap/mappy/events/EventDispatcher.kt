package info.journeymap.mappy.events

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions

private val logger = KotlinLogging.logger("events/dispatcher")


object EventDispatcher : EventListener {
    private val functionMap: MutableMap<KClass<out GenericEvent>, MutableList<Pair<KFunction<*>, Any>>> = mutableMapOf()

    fun register(clazz: Any) {
        for (function: KFunction<*> in clazz::class.functions) {
            val annotation: Listener? = function.findAnnotation<Listener>()

            if (annotation != null) {
                if (annotation.value !in this.functionMap || this.functionMap[annotation.value] == null) {
                    this.functionMap[annotation.value] = mutableListOf()
                }

                this.functionMap[annotation.value]!!.add(Pair(function, clazz))
            }
        }
    }

    override fun onEvent(event: GenericEvent) {
        val functions = functionMap[event::class] ?: listOf<Pair<KFunction<*>, Any>>()

        for (pair in functions.toList()) {
            // Immutable list copy so we aren't going to have changes while things run

            GlobalScope.launch {
                try {
                    val function = pair.first
                    val clazz = pair.second

                    function.callSuspend(clazz, event)
                } catch (t: Throwable) {
                    logger.error(t) { "Error thrown in event listener" }
                }
            }
        }
    }
}
