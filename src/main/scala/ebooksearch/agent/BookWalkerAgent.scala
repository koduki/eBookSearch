package cn.orz.pascal.scala.ebooksearch.agent
import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.commons.utils.XmlUtils._
import se.fishtank.css.selectors.Selectors._
import cn.orz.pascal.scala.mechanize._
import scala.xml.NodeSeq
import scala.xml.Node
// vim: set ts=2 sw=2 et:
class BookWalkerAgent extends SimpleAgent {
  override val provider = Providers.bookWalker
  
  override def getNewItems(): List[Item] = {
    val agent = new Mechanize()
    val page = agent.get("http://bookwalker.jp/pc/new/")
    val nodes = page.get(Class("sectionWrap")) $ "[class=itemWrap]"

    nodes.map { node =>
      Thread.sleep(500)
      val url = (node $ "[class=title] > a") attr "href"
      val itemPage = agent.get(url)
      debug("url:%s".format(url))

      (url, itemPage.get(Class("section itemDetail")))
    }.map { page =>
      val node = page._2
      val title = (node $ "h1").text.trim
      val url = page._1
      val value = (node $ "[class=price] > strong").map(_.text.trim.toInt).foldLeft(0) { (r, x) => r + x }
      val author = (node $ "[class=writer] > a").text.trim
      val author_url = (node $ "[class=writer] > a") attr "href"
      val image_url = (node $ "[class=image] > img") attr "src"

      Item(title, url, value, author, author_url, image_url, provider)
    }
  }

  override protected def read(keyword: String, pageNumber: Int): Option[NodeSeq] = {
    val agent = new Mechanize()
    agent.isJavaScriptEnabled_=(true)
    val queryUrl = "http://bookwalker.jp/pc/search/?detail=1&order=rank&disp=30&word=" + utf8(keyword) + "&inc=1&page=" + pageNumber;
    debug("url:%s, keyword:%s, encode:%s".format(queryUrl, keyword, utf8(keyword)).replaceAll("\n", ""))

    val page = agent.get(queryUrl)
      Thread.sleep(1500)
    val pager = page.get(Class("pageSelect"))
    if (pager != null) {
      this._hasNext = (!((pager $ "li").isEmpty) && !((pager $ "li").last \ "a").isEmpty)
      val nodes = page.get(Id("section-search")) $ "[class=itemWrap]"
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

