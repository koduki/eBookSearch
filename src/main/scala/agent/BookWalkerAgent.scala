package cn.orz.pascal.scala.ebooksearch.agent
import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.ebooksearch.utils.LoggingSupport
import se.fishtank.css.selectors.Selectors._
import cn.orz.pascal.scala.mechanize._
import scala.xml.NodeSeq

// vim: set ts=2 sw=2 et:
class BookWalkerAgent extends SimpleAgent {
  override val provider = Provider("BOOK☆WALKER", "http://bookwalker.jp/")

  def getNewBooks(): List[Item] = {
    val agent = new Mechanize()
    val page = agent.get("http://bookwalker.jp/pc/new/")
    val nodes = page.get(Class("sectionWrap")) $ "[class=itemWrap]"

    nodes.map { node =>
      Thread.sleep(500)
      val url = (node $ "[class=title] > a")(0).attribute("href") match { case Some(x) => x.toString; case _ => "" }
      val itemPage = agent.get(url)
      debug("url:%s".format(url))

      (url, itemPage.get(Class("section itemDetail")))
    }.map { page =>
      val node = page._2
      val title = (node $ "h1").text.trim
      val url = page._1
      val value = (node $ "[class=price] > strong").map(_.text.trim.toInt).foldLeft(0) { (r, x) => r + x }
      val author = (node $ "[class=writer] > a").text.trim
      val author_url = (node $ "[class=writer] > a")(0).attribute("href") match { case Some(x) => x.toString; case _ => "" }
      val image_url = (node $ "[class=image] > img")(0).attribute("src") match { case Some(x) => x.text; case _ => "" }

      Item(title, url, value, author, author_url, image_url, provider)
    }
  }

  override def getItemNodes(page: HtmlPage): NodeSeq = {
    Thread.sleep(1000)
    page.get(Id("section-search")) $ "[class=itemWrap]"
  }

  override def read(keyword: String): Option[NodeSeq] = {
    val agent = new Mechanize()
    val queryUrl = "http://bookwalker.jp/pc/search/?detail=1&order=rank&disp=30&word=" + encode(keyword) + "&inc=1";
    debug("url:%s, keyword:%s, encode:%s".format(queryUrl, keyword, encode(keyword)).replaceAll("\n", ""))

    val page = agent.get(queryUrl)

    val pager = page.get(Class("pageSelect"))
    if (pager != null) {
      val pageNumbers = (pager $ "li > a[class=page-numbers]").map { node => node.text.trim.toInt }
      val pageCount = if (pageNumbers.isEmpty) { 1 } else { pageNumbers.max }

      Some(readPages(agent, queryUrl, pageCount, "&page="))
    } else {
      None
    }
  }

  override def parse(itemNodes: NodeSeq): List[Item] = {
    itemNodes.map { node =>
      val title = (node $ "[class=detail] > [class=title] > a").text.trim
      val url = (node $ "[class=detail] > [class=title] > a")(0).attribute("href") match { case Some(x) => x.text; case _ => "" }
      val value = (node $ "[class=price] > strong").text.trim.toInt
      val author = (node $ "[class=detail] > [class=writer]").text.trim
      val author_url = ""
      val image_url = (node $ "[class=image] > a > img")(0).attribute("src") match { case Some(x) => x.text; case _ => "" }

      Item(title, url, value, author, author_url, image_url, provider)
    }.toList

  }

}

