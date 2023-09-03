package com.portalsoup.saas.core.quartz.job

import com.portalsoup.saas.core.config.AppConfig
import com.portalsoup.saas.core.service.PriceChartingManager
import org.quartz.Job
import org.quartz.JobExecutionContext

class PriceChartingUpdateJob(val appConfig: AppConfig): Job {
    override fun execute(context: JobExecutionContext?) {
        PriceChartingManager(appConfig).updateLoosePriceGuide()
    }
}