package cn.orz.pascal.ebooksearch.agent
import cn.orz.pascal.ebooksearch.models._
import cn.orz.pascal.commons.utils.LoggingSupport
import se.fishtank.css.selectors.Selectors._
import cn.orz.pascal.mechanize._
import scala.xml.NodeSeq

// vim: set ts=2 sw=2 et:
trait SimpleAgent extends Agent with LoggingSupport {
  val provider: Provider
  var _hasNext = false
  def hasNext: Boolean = this._hasNext

  override def search(keyword: String, pageNumber: Int = 1): (List[Item], Boolean) = {
    read(keyword, pageNumber) match {
      case Some(nodes) => (parse(nodes), this.hasNext)
      case None => (List(), false)
    }
  }

  protected def utf8(keyword: String) = java.net.URLEncoder.encode(keyword, "UTF-8")
  protected def sjis(keyword: String) = java.net.URLEncoder.encode(keyword, "Shift_JIS")
  protected def read(keyword: String, pageNumber: Int): Option[NodeSeq]
  protected def parse(nodes: NodeSeq): List[Item]

}
