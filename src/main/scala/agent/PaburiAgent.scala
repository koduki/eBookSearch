package cn.orz.pascal.scala.ebooksearch.agent
import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.ebooksearch.utils.LoggingSupport
import se.fishtank.css.selectors.Selectors._
import cn.orz.pascal.scala.mechanize._
import scala.xml.NodeSeq

// vim: set ts=2 sw=2 et:
class PaburiAgent extends SimpleAgent {
  override val provider = Provider("Paburi", "http://www.paburi.com/paburi/")

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
    page.get(Id("search-result")) $ "ul[class=clearfix] > li"
  }

  override def read(keyword: String): Option[NodeSeq] = {
    val agent = new Mechanize()
    agent.isJavaScriptEnabled_=(false)
    val queryUrl = "http://www.paburi.com/paburi/bin/qfind2.asp?pack=100&sort=0&keyword=" + sjis(keyword) + "&environment=";
    debug("url:%s, keyword:%s, encode:%s".format(queryUrl, keyword, sjis(keyword)).replaceAll("\n", ""))

    val page = agent.get(queryUrl)

    val pager = (page.get(Id("search-result")) $ "[class=result-number]")
    if (!pager.isEmpty) {
      val resultNumber = pager.text.trim.split(" ")(0).toInt
      val pageCount = resultNumber / 100 + 1

      Some(readPages(agent, queryUrl, pageCount, "&page="))
    } else {
      None
    }
  }

  override def parse(itemNodes: NodeSeq): List[Item] = {
    val baseUrl = "http://www.paburi.com"

    itemNodes.map { node =>
      val title = (node $ "[class=bk-title] > a").text.trim
      val url = (node $ "[class=bk-title] > a")(0).attribute("href") match { case Some(x) => x.text; case _ => "" }
      val value = (node $ "[class=bk-price] > strong").text.trim.toInt
      val author = (node $ "[class=bk-author]").text.trim
      val author_url = ""
      val image_url = (node $ "[class=bk-thumb] > a > img")(0).attribute("src") match { case Some(x) => x.text; case _ => "" }

      Item(title, url, value, author, author_url, baseUrl + image_url, provider)
    }.toList

  }

}

