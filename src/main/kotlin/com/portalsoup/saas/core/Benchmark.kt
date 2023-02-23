package com.portalsoup.saas.core

import java.time.Duration

data class BenchmarkResult<T>(val duration: Duration, val payload: T?)

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
