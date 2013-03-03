package cn.orz.pascal.ebooksearch.models

// vim: set ts=2 sw=2 et:
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection
import java.util.Date

case class AccessLog(
  @Key("_id") id: ObjectId = new ObjectId,
  val host: String,
  val user: String,
  val method: String,
  val path: String,
  val code: Int,
  val size: Int,
  val referer: String,
  val agent: String,
  val time: Date
)

object AccessLogDao extends SalatDAO[AccessLog, ObjectId](collection = MongoConnection()("test")("access_logs"))
