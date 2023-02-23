package com.portalsoup.saas.quartz

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.extensions.log
import com.portalsoup.saas.scheduler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.quartz.CronScheduleBuilder
import org.quartz.CronScheduleBuilder.dailyAtHourAndMinute
import org.quartz.Job
import org.quartz.JobBuilder.newJob
import org.quartz.TriggerBuilder.newTrigger

object QuartzModule: KoinComponent, Logging {

    private val appConfig by inject<AppConfig>()

    fun init() {
        log().info("initializing ")
        if (appConfig.pricechartingToken.isNotEmpty()) {
            initJob(
                PriceChartingUpdateJob::class.java,
                "update-price-guide",
                "pricecharting",
                dailyAtHourAndMinute(0, 0)
            )
        }
    }

    @Suppress("SameParameterValue")
    private fun initJob(job: Class<out Job>, identity: String, group: String, schedule: CronScheduleBuilder) {
        val newJob = newJob(job)
            .withIdentity("job-$identity", group)
            .build()

        val trigger = newTrigger()
            .withIdentity("trigger-$identity", group)
            .withSchedule(schedule)
            .build()

        scheduler.scheduleJob(newJob, trigger)
    }
}