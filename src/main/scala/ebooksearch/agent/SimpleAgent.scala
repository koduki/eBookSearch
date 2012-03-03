package cn.orz.pascal.scala.ebooksearch.agent
import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.commons.utils.LoggingSupport
import se.fishtank.css.selectors.Selectors._
import cn.orz.pascal.scala.mechanize._
import scala.xml.NodeSeq

// vim: set ts=2 sw=2 et:
trait SimpleAgent extends Agent with LoggingSupport {
  val provider: Provider

  override def getNewItems(): List[Item]
  override def search(keyword: String): List[Item] = {
    read(keyword) match {
      case Some(nodes) => parse(nodes)
      case None => List()
    }

  }

  protected def utf8(keyword: String) = java.net.URLEncoder.encode(keyword, "UTF-8")
  protected def sjis(keyword: String) = java.net.URLEncoder.encode(keyword, "Shift_JIS")
  protected def getItemNodes(page: HtmlPage): NodeSeq
  protected def read(keyword: String): Option[NodeSeq]
  protected def parse(itemNodes: NodeSeq): List[Item]
  protected def readPages(agent: Mechanize, url: String, pageCount: Int, pageOption: String): NodeSeq = {
    val blank = <blank/> \ "any"
    (1 to pageCount).map { i =>
      val queryUrl = url + pageOption + i
      debug("url:%s, count:%d".format(queryUrl, pageCount).replaceAll("\n", ""))

      getItemNodes(agent.get(queryUrl))
    }.foldLeft(blank)((r, node) => r ++ node)
  }

}
