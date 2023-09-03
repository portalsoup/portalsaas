package com.portalsoup.saas.common.util

import java.time.Duration

data class BenchmarkResult<T>(val duration: Duration, val payload: T?)

/**
 * A basic function used to measure the execution time of work
 *
 * @param lambda A function to execute and measure
 */
inline fun <reified T> measureDuration(lambda: () -> T): BenchmarkResult<T> {
    val before = System.nanoTime()
    val result = lambda()
    val after = System.nanoTime()
    val duration = Duration.ofNanos(after - before)

    return if (result is Unit) {
        BenchmarkResult(duration, null)
    } else {
        BenchmarkResult(duration, result)
    }
}
