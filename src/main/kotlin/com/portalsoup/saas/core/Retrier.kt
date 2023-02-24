package com.portalsoup.saas.core

import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.extensions.log
import java.lang.Exception
import java.lang.RuntimeException

data class RetryConfig(
    val maxTries: Int = 5,
    val firstInterval: Int = 5,
    val nextBackoffInterval: (timesWaited: Int) -> Int = { it * 5 }, // default adds 5 seconds per wait
    val verbose: Boolean = false
)

/**
 * Allows a function to be executed, and if an exception is thrown, to be re-executed after a delay.
 */
object Retrier: Logging {

    /**
     * @param name A unique name to assign to this retry instance
     * @param config Configures the behavior of this retry instance
     * @param lambda A function that may fail with an exception to retry
     */
    operator fun <T> invoke(
        name: String,
        config: RetryConfig = RetryConfig(),
        lambda: () -> T
    ): T {
        for (x in 1..config.maxTries) {
            log().info("Attempting to run $name...")
            try {
                val success = lambda()
                log().info("Success!")
                return success
            } catch (e: Exception) {
                val shortMsg = "Failed because: : [${e.message}] with ${config.maxTries - x} tries remaining.  Waiting for ${config.nextBackoffInterval(x)} seconds"
                val longMsg = "$shortMsg\n${e.stackTraceToString()}"
                log().info(config.verbose.takeIf { true }?.let { longMsg } ?: shortMsg)
                Thread.sleep((config.firstInterval * 1000L) + config.nextBackoffInterval(x)) // interval + 5 seconds per try
            }
        }
        throw RetryException("$name failed ${config.maxTries} times.")
    }
}

data class RetryException(val str: String, val e: Exception? = null) : RuntimeException(str, e)
