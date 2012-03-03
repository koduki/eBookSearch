package cn.orz.pascal.scala.ebooksearch.agent
import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.commons.utils.LoggingSupport
import cn.orz.pascal.scala.commons.utils.XmlUtils._
import se.fishtank.css.selectors.Selectors._
import cn.orz.pascal.scala.mechanize._
import scala.xml.NodeSeq
import scala.xml.Node
import cn.orz.pascal.scala.commons.utils.XmlUtils._
// vim: set ts=2 sw=2 et:
class PaburiAgent extends SimpleAgent {
  override val provider = Provider("Paburi", "http://www.paburi.com/paburi/")

  override def getNewItems(): List[Item] = {
    val newsUrl = "http://www.paburi.com/paburi/bin/qfind2.asp?pack=100&sort=1&newest=1&order=4"
    val agent = new Mechanize()
    agent.isJavaScriptEnabled_=(false)
    val page = agent.get(newsUrl)
    debug("url:%s".format(newsUrl))
    val nodes = page.get(Id("search-result")) $ "ul[class=clearfix] > li"

    nodes.map { parseNode(_) }
  }

  private def parseNode(node: Node) = {
    val baseUrl = "http://www.paburi.com"

    val title = (node $ "[class=bk-title] > a").text.trim
    val url = (node $ "[class=bk-title] > a") attr "href"
    val value = (node $ "[class=bk-price] > strong").text.trim.toInt
    val author = (node $ "[class=bk-author]").text.trim
    val author_url = ""
    val image_url = (node $ "[class=bk-thumb] > a > img") attr "src"

    Item(title, url, value, author, author_url, baseUrl + image_url, provider)
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
    itemNodes.map { parseNode(_) }.toList
  }

}

