package com.portalsoup.saas.quartz.job

import com.portalsoup.saas.manager.RssManager
import org.quartz.Job
import org.quartz.JobExecutionContext

class RssPoller: Job {
    override fun execute(context: JobExecutionContext?) {
        RssManager().rssPoller()
    }
}