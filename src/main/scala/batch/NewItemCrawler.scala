package cn.orz.pascal.scala.ebooksearch.batch

// vim: set ts=2 sw=2 et:
import cn.orz.pascal.scala.ebooksearch.searchagent._
import cn.orz.pascal.scala.ebooksearch.models._

class NewItemCrawler {
  def crawl() {
    val ebookJapan = new EBookJapanSearchAgent()
    val nicoSeiga = new NicoSeigaSearchAgent()
    val items = ebookJapan.getNewBooks ++ nicoSeiga.getNewBooks

    for(item <- items) {
      FeedItemDao.insert(FeedItem(item, new java.util.Date()))
    }
  }
}
