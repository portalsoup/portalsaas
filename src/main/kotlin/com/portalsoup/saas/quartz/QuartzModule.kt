package com.portalsoup.saas.quartz

import com.portalsoup.saas.scheduler
import org.quartz.CronScheduleBuilder
import org.quartz.CronScheduleBuilder.cronSchedule
import org.quartz.CronScheduleBuilder.dailyAtHourAndMinute
import org.quartz.Job
import org.quartz.JobBuilder.newJob
import org.quartz.TriggerBuilder.newTrigger

object QuartzModule {

    fun init() {
        println("initializing ")
        initJob(PriceChartingUpdateJob::class.java, "update-price-guide", "pricecharting", dailyAtHourAndMinute(0, 0))
    }

    private fun initJob(job: Class<out Job>, identity: String, group: String, schedule: CronScheduleBuilder) {
        val job = newJob(job)
            .withIdentity("job-$identity", group)
            .build();

        val trigger = newTrigger()
            .withIdentity("trigger-$identity", group)
            .withSchedule(schedule)
            .build()

        scheduler.scheduleJob(job, trigger)
    }
}