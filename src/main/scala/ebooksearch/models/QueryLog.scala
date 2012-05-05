package cn.orz.pascal.scala.ebooksearch.models

// vim: set ts=2 sw=2 et:
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection

case class QueryLog(qury: String, items: List[Item], createdAt: java.util.Date)
object QueryLogDao extends SalatDAO[QueryLog, Int](collection = MongoConnection()("test")("query_logs"))

