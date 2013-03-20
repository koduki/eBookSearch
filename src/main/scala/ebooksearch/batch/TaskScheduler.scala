package cn.orz.pascal.ebooksearch.batch

// vim: set ts=2 sw=2 et:
import javax.servlet.http.HttpServlet
import org.quartz._
import org.quartz.impl.StdSchedulerFactory
import org.quartz.JobBuilder.newJob
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import cn.orz.pascal.commons.utils.LoggingSupport

class NewItemCrawlerJob extends LoggingSupport {
  def execute() {
    loggingTime(
      "START_JOB: clawling") {
        val crawler = new NewItemCrawler()
        crawler.crawl
      }("END_JOB: clawling, proc time: %time (ms)")
  }
}
