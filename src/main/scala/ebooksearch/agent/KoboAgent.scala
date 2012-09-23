package cn.orz.pascal.ebooksearch.agent
import cn.orz.pascal.ebooksearch.models._
import cn.orz.pascal.commons.utils.XmlUtils._
import se.fishtank.css.selectors.Selectors._
import cn.orz.pascal.mechanize._
import scala.xml.NodeSeq
import scala.xml.Node

// vim: set ts=2 sw=2 et:
class KoboAgent extends SimpleAgent {
  override def provider = Providers.kobo

  override def getNewItems(): List[Item] = {

    val agent = new Mechanize()

    def get(pageNum: Int) = {
      val url = "http://www.ebookjapan.jp/ebj/newlist.asp?genre_request=0&page=" + pageNum.toString
      debug("url:%s".format(url))

      val page = agent.get(url)
      val main_line = page.get(Id("main_line"))
      val item_nodes = (main_line \\ "li").filter(item => (item \ "@class" text) == "heightLineChangeable")

      parse(item_nodes)
    }

    (1 to 5).map(get(_)).toList.flatten
  }

  override protected def read(keyword: String, pageNumber: Int): Option[NodeSeq] = {
    val agent = new Mechanize()
    agent.isJavaScriptEnabled_=(false)
    val queryUrl = "http://rakuten.kobobooks.com/search/search.html?q=" + utf8(keyword) + "&t=&f=keyword&p=" + pageNumber + "&s=&g=both&l="
    debug("url:%s, keyword:%s, encode:%s".format(queryUrl, keyword, sjis(keyword)).replaceAll("\n", ""))

    val page = agent.get(queryUrl)
    val nodes = page.asXml $ "li.SCSearchItem"

    if (!nodes.isEmpty) {
      val iconNext = page.get(Class("iconNext"))
      this._hasNext = (iconNext != null)
      Some(nodes)
    } else {
      None
    }
  }

  override protected def parse(nodes: NodeSeq): List[Item] = {
    nodes.map { node =>
      val title = node \\ "h3" \ "@title" text
      val url = "http://rakuten.kobobooks.com" + ((node \\ "h3" \ "a").last \ "@href" text)
      val author = (node $ ".Author>span>a" text).trim
      val author_url = "http://rakuten.kobobooks.com" + ((node $ ".Author>span>a").first \ "@href" text)
      val value = (node $ ".KV2OurPrice>strong").text.replaceAll("円", "").replaceAll(",", "").trim.toInt
      val image_url = (node \\ "img" \ "@src" text)
      Item(title, url, value, author, author_url, image_url, provider)
    }.toList
  }

}
