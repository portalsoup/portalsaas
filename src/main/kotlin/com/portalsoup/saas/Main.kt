package com.portalsoup.saas

import io.ktor.server.netty.*
import org.quartz.impl.StdSchedulerFactory

fun main(args: Array<String>) {
    scheduler.start()
    EngineMain.main(args)
    scheduler.shutdown()
}

val scheduler = StdSchedulerFactory.getDefaultScheduler() ?: throw RuntimeException("Failed to initialize quartz factory")

