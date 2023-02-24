package com.portalsoup.saas.core

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class BenchmarkKtTest {


    @Test
    fun testBenchmarkWithNoReturn() {
        // Setup
        val sleepLength = 5000L
        val f = { Thread.sleep(sleepLength) }

        // Execution
        val result = measureDuration(f)

        // Verification
        println(result.duration.toMillis())
        assertTrue { result.duration.toMillis() > sleepLength }
        assertTrue { result.duration.toMillis() < sleepLength  + 50L} // A bit of time goes to the overhead of the execution, but shouldn't be much more than 5-10ms extra reasonably
    }

    @Test
    fun testBenchmarkWithReturn() {
        // Setup
        val expectedValue = "I am a value"
        val f = { expectedValue }

        // Execution
        val result = measureDuration(f)

        // Verification
        assertTrue { result.payload.equals(expectedValue) }
    }
}