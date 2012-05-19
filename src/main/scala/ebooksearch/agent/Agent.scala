package cn.orz.pascal.ebooksearch.agent
import cn.orz.pascal.ebooksearch.models._
import cn.orz.pascal.commons.utils.LoggingSupport

// vim: set ts=2 sw=2 et:
trait Agent {
  def search(keyword: String, pageNumber:Int): (List[Item], Boolean)
  def getNewItems(): List[Item]
}