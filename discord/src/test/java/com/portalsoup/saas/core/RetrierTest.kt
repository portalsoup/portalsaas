package com.portalsoup.saas.core

import com.portalsoup.saas.util.Retrier
import com.portalsoup.saas.util.RetryConfig
import com.portalsoup.saas.util.RetryException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RetrierTest {

    @Test
    fun retryTestFail() {
        // Setup
        val f: () -> String = {
            throw RuntimeException("This will always fail")
        }

        // Execution / Verification
        assertThrows(RetryException::class.java) {
            Retrier(
                name = "failTest",
                config = RetryConfig(maxTries = 2, 0, { 0 }),
                lambda = f
            )
        }
    }

    @Test
    fun retrySuccessAfterFail() {
        // Setup
        var firstRun = true
        val response = "This is a response"
        val f: () -> String = {
            if (firstRun) {
                firstRun = false
                throw RuntimeException("The first run will always fail")
            }else {
                response
            }
        }

        // Execution
        val result = Retrier(
            name = "failTest",
            config = RetryConfig(maxTries = 2, 0, { 0 }),
            lambda = f
        )

        // Verification
        assertTrue { result == response }
    }
}