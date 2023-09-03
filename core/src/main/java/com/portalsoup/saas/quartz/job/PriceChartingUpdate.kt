package com.portalsoup.saas.quartz.job

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.service.PriceChartingManager
import org.quartz.Job
import org.quartz.JobExecutionContext

class PriceChartingUpdateJob(val appConfig: AppConfig): Job {
    override fun execute(context: JobExecutionContext?) {
        PriceChartingManager(appConfig).updateLoosePriceGuide()
    }
}