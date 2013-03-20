package cn.orz.pascal.ebooksearch.batch

// vim: set ts=2 sw=2 et:
import cn.orz.pascal.ebooksearch.agent._
import cn.orz.pascal.ebooksearch.models._
import cn.orz.pascal.commons.utils.LoggingSupport

class NewItemCrawler extends LoggingSupport {
  def getKobo() = crawl(List(new KoboAgent))
  def getBookWlaker() = crawl(List(new BookWalkerAgent))
  def getPaburiAgent() = crawl(List(new PaburiAgent))

  def crawl(agents:List[Agent]) {
    val items = agents.map { agent =>
      loggingTime(
        "start clawling - " + agent.provider.name) {
          agent.getNewItems
        }("end clawling - " + agent.provider.name + ", proc time: %time (ms)")
    }.fold(List[Item]()) { (r, x) => r ++ x }

    for (item <- items) {
      FeedItemDao.insert(FeedItem(item, new java.util.Date()))
    }
  }
}
