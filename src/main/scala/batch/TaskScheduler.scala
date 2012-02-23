package cn.orz.pascal.scala.ebooksearch.batch

// vim: set ts=2 sw=2 et:
import javax.servlet.http.HttpServlet
import org.quartz._
import org.quartz.impl.StdSchedulerFactory
import org.quartz.JobBuilder.newJob
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import cn.orz.pascal.scala.ebooksearch.utils.LoggingSupport

class NewItemCrawlerJob extends Job with LoggingSupport {
  def execute(context: JobExecutionContext) {
    val crawler = new NewItemCrawler()
    info("start clawling")
    crawler.crawl
    info("end clawling")
  }
}
object NewItemCrawlerJob {
  def schdule = simpleSchedule.repeatForever.withIntervalInHours(24)
}

class TaskScheduler extends HttpServlet {
  // スケジューラー作成
  val scheduler = StdSchedulerFactory.getDefaultScheduler();

  def regist() {
    // ジョブの作成
    val job = newJob(classOf[NewItemCrawlerJob]).build();
    val trigger = newTrigger().withSchedule(NewItemCrawlerJob.schdule).build();

    // トリガーとともにジョブをスケジューラーに登録
    scheduler.scheduleJob(job, trigger);
  }

  override def init() {
    try {
      scheduler.start();
      regist()
    } catch {
      case e: SchedulerException => e.printStackTrace()
    }
  }

}
