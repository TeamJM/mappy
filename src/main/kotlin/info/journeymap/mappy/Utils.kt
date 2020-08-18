package info.journeymap.mappy

import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.rest.request.RestRequestException
import io.ktor.http.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.nibor.autolink.LinkSpan

fun LinkSpan.getScheme(content: String): String? {
    val domain = content.substring(beginIndex, endIndex)

    if ("://" in domain) {
        return domain.split("://")[0]
    }

    return null
}

fun LinkSpan.getDomain(content: String): String {
    var domain = content.substring(beginIndex, endIndex)

    if ("://" in domain) {
        domain = domain.split("://")[1]
    }

    if ("/" in domain) {
        domain = domain.split("/").last()
    }

    return domain
}

/**
 * Deletes a message, catching and ignoring a HTTP 404 (Not Found) exception.
 */
suspend fun Message.deleteIgnoringNotFound() {
    try {
        this.delete()
    } catch (e: RestRequestException) {
        if (e.code != HttpStatusCode.NotFound.value) {
            throw e
        }
    }
}

/**
 * Deletes a message after a delay.
 *
 * This function **does not block**.
 *
 * @param millis The delay before deleting the message, in milliseconds.
 * @return Job spawned by the CoroutineScope.
 */
fun Message.deleteWithDelay(millis: Long, retry: Boolean = true): Job {
    val logger = KotlinLogging.logger {}

    return this.kord.launch {
        delay(millis)

        try {
            this@deleteWithDelay.deleteIgnoringNotFound()
        } catch (e: RestRequestException) {
            val message = this@deleteWithDelay

            if (retry) {
                logger.debug(e) {
                    "Failed to delete message, retrying: $message"
                }

                this@deleteWithDelay.deleteWithDelay(millis, false)
            } else {
                logger.error(e) {
                    "Failed to delete message: $message"
                }
            }
        }
    }
}
