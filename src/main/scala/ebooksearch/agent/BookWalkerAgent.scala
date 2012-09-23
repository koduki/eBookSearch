// vim: set ts=2 sw=2 et:
package cn.orz.pascal.ebooksearch.agent
import cn.orz.pascal.ebooksearch.models._
import cn.orz.pascal.commons.utils.XmlUtils._
import se.fishtank.css.selectors.Selectors._
import cn.orz.pascal.mechanize._
import scala.xml.NodeSeq
import scala.xml.Node

class BookWalkerAgent extends SimpleAgent {
  override def provider = Providers.bookWalker

  override def getNewItems(): List[Item] = {
    val agent = new Mechanize()
    agent.isJavaScriptEnabled_=(true)

    val categories = List(
      ("ct3" -> "ライトノベル"),
      ("ct2" -> "コミック"),
      ("ct1" -> "文芸"),
      ("ct5" -> "新書"),
      ("ct4" -> "実用書"),
      //      ("ct7" -> "画集"),
      ("ct6" -> "ゲーム攻略本"),
      ("ct102" -> "ケータイ小説"))

    def getItems(category: String) = {
      (1 to 3).map { pageNumber =>
        val queryUrl = "http://bookwalker.jp/pc/list/category/" + category + "?detail=1&order=release&disp=40&page=" + pageNumber;
        debug("url:%s".format(queryUrl))

        val page = agent.get(queryUrl)
        Thread.sleep(1500)
        val nodes = (page.asXml $ ".itemWrap")
        info("url:%s, size:%s".format(queryUrl, nodes.size))

        parse(nodes)
      }.toList.flatten
    }
    categories.reverse.map { c => getItems(c._1) }.flatten
  }

  override protected def read(keyword: String, pageNumber: Int): Option[NodeSeq] = {
    val agent = new Mechanize()
    agent.isJavaScriptEnabled_=(true)
    val queryUrl = "http://bookwalker.jp/pc/search/?detail=1&order=rank&disp=40&word=" + utf8(keyword) + "&inc=1&page=" + pageNumber;
    debug("url:%s, keyword:%s, encode:%s".format(queryUrl, keyword, utf8(keyword)).replaceAll("\n", ""))

    val page = agent.get(queryUrl)
    Thread.sleep(1500)
    val pager = page.get(Class("pageSelect"))
    if (pager != null) {
      this._hasNext = (!((pager $ "li").isEmpty) && !((pager $ "li").last \ "a").isEmpty)
      val nodes = (page.asXml $ ".itemWrap")
      debug("found :" + keyword + ", size:" + nodes.size)

      Some(nodes)
    } else {
      debug("not found :" + keyword)
      None
    }
  }

  override protected def parse(nodes: NodeSeq): List[Item] = {
    nodes.map { node =>
      val title = (node $ "[class=detail] > [class=title] > a").text.trim
      val url = (node $ "[class=detail] > [class=title] > a") attr "href"
      val value = (node $ "[class=price] > strong").text.trim.toInt
      val author = (node $ "[class=detail] > [class=writer]").text.trim
      val author_url = ""
      val image_url = (node $ "[class=image] > a > img") attr "src"

      Item(title, url, value, author, author_url, image_url, provider)
    }.toList
  }

}

