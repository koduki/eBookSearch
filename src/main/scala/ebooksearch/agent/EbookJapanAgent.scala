package cn.orz.pascal.scala.ebooksearch.agent
import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.commons.utils.XmlUtils._
import se.fishtank.css.selectors.Selectors._
import cn.orz.pascal.scala.mechanize._
import scala.xml.NodeSeq
import scala.xml.Node

// vim: set ts=2 sw=2 et:
class EBookJapanAgent extends SimpleAgent {
  override val provider = Providers.eBookJapan

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
    val queryUrl = "http://www.ebookjapan.jp/ebj/search.asp?s=6&sd=0&ebj_desc=on&q=" + sjis(keyword) + "&page=" + pageNumber;
    debug("url:%s, keyword:%s, encode:%s".format(queryUrl, keyword, sjis(keyword)).replaceAll("\n", ""))

    val page = agent.get(queryUrl)

    val nodes = page.get(Id("main_line")) \\ "li"
    if (!nodes.isEmpty) {
      val pager = page.get(Class("pager_warp"))
      this._hasNext = if (pager != null) {
        val next = (pager $ "a > img" last).attribute("src") match { case Some(x) => x.text; case _ => "" }
        (next == "/commonnew/image/common/next.gif")
      } else {
        false
      }

      Some(nodes)
    } else {
      None
    }
  }

  override protected def parse(nodes: NodeSeq): List[Item] = {
    nodes.map { node =>
      val title = (node \ "h5" \ "a").text.trim
      val url = "http://www.ebookjapan.jp" + (node \ "h5" \ "a" \ "@href").text
      val value = (node \ "h6")(0).child(0).text.trim.replaceAll("円.*", "").toInt
      val author = ((node \ "div")(1) \ "a").text.trim
      val author_url = "http://www.ebookjapan.jp" + ((node \ "div")(1) \ "a" \ "@href").text
      val image_url = (node \\ "img" \ "@src").text

      Item(title, url, value, author, author_url, image_url, provider)
    }.toList
  }

}
