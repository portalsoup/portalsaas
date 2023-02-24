package com.portalsoup.saas.quartz.job

import com.portalsoup.saas.manager.PriceChartingManager
import org.quartz.Job
import org.quartz.JobExecutionContext

class PriceChartingUpdateJob: Job {
    override fun execute(context: JobExecutionContext?) {
        PriceChartingManager().updateLoosePriceGuide()
    }
}