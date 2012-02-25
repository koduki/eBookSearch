package cn.orz.pascal.scala.ebooksearch.agent
import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.ebooksearch.utils.LoggingSupport

// vim: set ts=2 sw=2 et:
class EBookJapanAgent extends Agent with LoggingSupport {
  val provider = Provider("eBookJapan", "http://www.ebookjapan.jp/")

  def search(keyword: String): List[Item] = {
    parse(read(keyword))
  }

  def getNewBooks(): List[Item] = {
    import cn.orz.pascal.scala.mechanize._
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

  def read(keyword: String): scala.xml.NodeSeq = {
    import cn.orz.pascal.scala.mechanize._

    def encode(keyword: String) = java.net.URLEncoder.encode(keyword, "SJIS")
    def readPages(agent: Mechanize, url: String, pageCount: Int) = {
      val blank = <blank/> \ "any"

      (1 to pageCount).map { i =>
        val page = agent.get(url + "&page=" + i)
        page.get(Id("main_line")) \\ "li"
      }.foldLeft(blank)((r, node) => r ++ node)
    }

    val agent = new Mechanize()
    val queryUrl = "http://www.ebookjapan.jp/ebj/search.asp?s=6&sd=0&ebj_desc=on&q=" + encode(keyword)
    val page = agent.get(queryUrl)
    val navi = page.get(Class("pagenavi"))
    val pageCount = if (navi != null) {
      "(全)(.*?)(ページ)".r.findFirstMatchIn(navi.text).get.group(2).toInt
    } else {
      -1
    }

    debug("url:%s, keyword:%s, encode:%s, count:%d".format(page.url, keyword, encode(keyword), pageCount).replaceAll("\n", ""))

    readPages(agent, queryUrl, pageCount)
  }

  def parse(item_nodes: scala.xml.NodeSeq): List[Item] = {
    item_nodes.map(item =>
      Item(
        (item \ "h5" \ "a").text.trim,
        "http://www.ebookjapan.jp" + (item \ "h5" \ "a" \ "@href").text,
        (item \ "h6")(0).child(0).text.trim.replaceAll("円.*", "").toInt,
        ((item \ "div")(1) \ "a").text.trim,
        "http://www.ebookjapan.jp" + ((item \ "div")(1) \ "a" \ "@href").text,
        (item \\ "img" \ "@src").text,
        provider)).toList
  }

}
