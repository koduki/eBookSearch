package cn.orz.pascal.scala.ebooksearch.searchagent
import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.ebooksearch.utils.LoggingSupport

// vim: set ts=2 sw=2 et:
trait SearchAgent { def search(keyword:String):List[Item] }
