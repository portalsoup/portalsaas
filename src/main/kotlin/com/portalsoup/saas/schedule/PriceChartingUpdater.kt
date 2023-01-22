package com.portalsoup.saas.schedule

import com.portalsoup.saas.config.PriceChartingConfig
import com.portalsoup.saas.manager.PriceChartingManager
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import java.util.concurrent.atomic.AtomicInteger

class PriceChartingUpdater() {
    class PriceChartingCsvJob : Job {

        companion object {
            private val atomicInt = AtomicInteger()

            val detail: JobDetail by lazy {
                JobBuilder.newJob(PriceChartingCsvJob::class.java)
                    .withIdentity("priceChartingCsv", "group1")
                    .build()
            }
        }
        override fun execute(context: JobExecutionContext?) {
            // make api call
            println("Tick ${atomicInt.incrementAndGet()}")
            PriceChartingManager().requestCsv()
            println("got csv")
        }
    }

    private val trigger: SimpleTrigger by lazy {
        TriggerBuilder.newTrigger()
            .withIdentity("priceChartingGuide", "group1")
            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(30)
                .repeatForever())
            .startNow()
            .build()
    }

    private val scheduler: Scheduler by lazy {
        val scheduler = StdSchedulerFactory.getDefaultScheduler()
        scheduler.scheduleJob(PriceChartingCsvJob.detail, trigger)
        scheduler
    }

    fun startScheduler() {
        scheduler.start()
    }
}