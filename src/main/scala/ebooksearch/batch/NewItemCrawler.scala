package cn.orz.pascal.scala.ebooksearch.batch

// vim: set ts=2 sw=2 et:
import cn.orz.pascal.scala.ebooksearch.agent._
import cn.orz.pascal.scala.ebooksearch.models._

class NewItemCrawler {
  def crawl() {
    val ebookJapan = new EBookJapanAgent
    val nicoSeiga = new NicoSeigaAgent
    val bookWalker = new BookWalkerAgent
    val items = bookWalker.getNewBooks ++ 
    ebookJapan.getNewBooks ++ 
    //nicoSeiga.getNewBooks
    List()

    for (item <- items) {
      FeedItemDao.insert(FeedItem(item, new java.util.Date()))
    }
  }
}
