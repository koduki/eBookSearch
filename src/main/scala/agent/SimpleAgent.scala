package cn.orz.pascal.scala.ebooksearch.agent
import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.ebooksearch.utils.LoggingSupport
import se.fishtank.css.selectors.Selectors._
import cn.orz.pascal.scala.mechanize._
import scala.xml.NodeSeq

// vim: set ts=2 sw=2 et:
trait SimpleAgent extends Agent with LoggingSupport {
  val provider:Provider
  
  def getNewBooks(): List[Item]
  def search(keyword: String): List[Item] = {
    read(keyword) match {
      case Some(nodes) => parse(nodes)
      case None => List()
    }
    
  }

  protected def encode(keyword: String) = java.net.URLEncoder.encode(keyword, "UTF-8")
  protected def getItemNodes(page: HtmlPage): NodeSeq
  protected def read(keyword: String):Option[NodeSeq]
  protected def parse(itemNodes: NodeSeq): List[Item]
  protected def readPages(agent: Mechanize, url: String, pageCount: Int, pageOption: String):NodeSeq = {
    val blank = <blank/> \ "any"
    (1 to pageCount).map { i =>
      val queryUrl = url + pageOption + i
      debug("url:%s, count:%d".format(queryUrl, pageCount).replaceAll("\n", ""))

      getItemNodes(agent.get(queryUrl))
    }.foldLeft(blank)((r, node) => r ++ node)
  }
  

}
