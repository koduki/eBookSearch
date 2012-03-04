package cn.orz.pascal.scala.ebooksearch.web

import org.scalatra._
import java.net.URL
import scalate.ScalateSupport
import com.mongodb.casbah.commons._
import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.commons.utils.LoggingSupport
import cn.orz.pascal.scala.commons.utils.DateUtils._
import cn.orz.pascal.scala.ebooksearch.agent._
import ch.qos.logback._
import org.slf4j._
import scala.actors.Futures._

class WebFront extends EBookSearchServlet {
  def validateParam(name: String, default: Boolean) = {
    if (params.contains(name) && (params(name).matches("[01]"))) {
      params(name) == "1"
    } else {
      default
    }
  }

  def validateParam(name: String, default: Int) = {
    if (params.contains(name) && params(name).matches("""\d""")) {
      params(name).toInt
    } else {
      default
    }
  }

  beforeAll {
    contentType = "text/html"
  }

  get("/") {
    val feeds = FeedItemDao
      .find(MongoDBObject("createdAt" -> MongoDBObject("$gte" -> today)))
      .sort(orderBy = MongoDBObject("createdAt" -> -1))
      .toList
    jade("index", "feeds" -> feeds)
  }

  get("/search") {
    val query = params('q)
    val pageNumber = validateParam("page", 1)
    var hasNextBKW = validateParam("bkw", true)
    var hasNextPBR = validateParam("pbr", true)
    var hasNextEBJ = validateParam("ebj", true)

    val results = List(
      new BookWalkerAgent,
      new PaburiAgent,
      new EBookJapanAgent)
      .map(x => future { x.search(query, pageNumber) })
      .map(_())
    val items = results.map { x => x._1 }.fold(List[Item]()) { (r, item) => r ++ item }
    val hasNexts = results.map { x => x._2 }
    hasNextBKW = hasNexts(0)
    hasNextPBR = hasNexts(1)
    hasNextEBJ = hasNexts(2)

    QueryLogDao.insert(QueryLog(query, items, new java.util.Date()))

    jade("search",
      "items" -> items,
      "pageNumber" -> pageNumber,
      "nextBkw" -> (if (hasNextBKW) { 1 } else { 0 }),
      "nextEbj" -> (if (hasNextEBJ) { 1 } else { 0 }),
      "nextPbr" -> (if (hasNextPBR) { 1 } else { 0 }))
  }

  notFound {
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }
}
