package com.portalsoup.saas.core.quartz

import com.portalsoup.saas.core.config.AppConfig
import com.portalsoup.saas.core.extensions.Logging
import com.portalsoup.saas.core.extensions.log
import com.portalsoup.saas.core.quartz.job.RssPoller
import org.quartz.*
import org.quartz.JobBuilder.newJob
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory
import java.util.*

/**
 * The entrypoint and configuration of Quartz jobs
 */
class QuartzModule(val appConfig: AppConfig): Logging {

    val scheduler = StdSchedulerFactory.getDefaultScheduler() ?: throw RuntimeException("Failed to initialize quartz factory")

    /**
     * Start all Quartz jobs
     */
    operator fun invoke() {
        log().info("initializing Quartz")
//        if (! appConfig.pricecharting.token.isNullOrEmpty()) {
//            initJob(
//                PriceChartingUpdateJob::class.java,
//                "update-price-guide",
//                "pricecharting",
//                dailyAtHourAndMinute(0, 0)
//            )
//        }

        initJob(
            RssPoller::class.java,
            "rss-poller",
            "rss",
            CronScheduleBuilder.cronSchedule("* 0/5 * * * ?")
        )

        scheduler.listenerManager.addTriggerListener(object : TriggerListener {
            var lastFireTime: Date? = null
            override fun getName() = "prevent-duplicates"

            override fun triggerFired(trigger: Trigger?, context: JobExecutionContext?) {}

            override fun vetoJobExecution(trigger: Trigger?, context: JobExecutionContext?): Boolean {
                val fireTime: Date? = context?.scheduledFireTime
                if (lastFireTime != null && fireTime != null && fireTime == lastFireTime) {
                    return true
                }
                lastFireTime = fireTime
                return false
            }

            override fun triggerMisfired(trigger: Trigger?) {
            }

            override fun triggerComplete(
                trigger: Trigger?,
                context: JobExecutionContext?,
                triggerInstructionCode: Trigger.CompletedExecutionInstruction?
            ) {
                }

        })
    }

    /**
     * Initialize a single Quartz job and schedule it for execution
     */
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