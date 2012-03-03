package cn.orz.pascal.scala.ebooksearch.agent
import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.commons.utils.LoggingSupport

// vim: set ts=2 sw=2 et:
trait Agent {
  def search(keyword: String): List[Item]
  def getNewBooks(): List[Item]
}