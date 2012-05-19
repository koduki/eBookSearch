package cn.orz.pascal.ebooksearch.batch

// vim: set ts=2 sw=2 et:
import cn.orz.pascal.ebooksearch.agent._
import cn.orz.pascal.ebooksearch.models._

class NewItemCrawler {
  def crawl() {
    val agents = List(
      new EBookJapanAgent,
      new BookWalkerAgent,
      new PaburiAgent)

    val items = agents.map { _.getNewItems() }.fold(List[Item]()) { (r, x) => r ++ x }
    for (item <- items) {
      FeedItemDao.insert(FeedItem(item, new java.util.Date()))
    }
  }
}
