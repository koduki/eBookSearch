package cn.orz.pascal.scala.ebooksearch.batch

// vim: set ts=2 sw=2 et:
import cn.orz.pascal.scala.ebooksearch.searcher._
import cn.orz.pascal.scala.ebooksearch.models._

class NewItemCrawler {
  def crawl() {
    val searcher = new EBookJapanSearcher()
    val items = searcher.getNewBooks
    for(item <- items) {
      FeedItemDao.insert(FeedItem(item, new java.util.Date()))
    }
  }
}
