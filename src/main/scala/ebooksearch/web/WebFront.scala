package cn.orz.pascal.scala.ebooksearch.web

import org.scalatra._
import java.net.URL
import scalate.ScalateSupport

import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.commons.utils.LoggingSupport
import cn.orz.pascal.scala.commons.utils.DateUtils._
import cn.orz.pascal.scala.ebooksearch.agent._
import ch.qos.logback._
import org.slf4j._
import scala.actors.Futures._
import com.mongodb.casbah.Imports._
import com.novus.salat.global._
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._

class WebFront extends BasicServlet {
  get("/") {
    def getFeeds(provider: Provider) = {
      FeedItemDao
        .find((MongoDBObject("_id.provider" -> grater[Provider].asDBObject(provider))))
        .sort(orderBy = MongoDBObject("createdAt" -> -1))
        .limit(8)
        .toList
        .foldLeft(Map[(Provider, java.util.Date), List[Item]]()) { (r, x) =>
          val createdAt = dateTrim(x.createdAt)
          val list = if (r.contains((provider, createdAt))) { r(provider, createdAt) } else { List[Item]() }
          r + ((provider, createdAt) -> (list ++ List(x.item)))
        }
    }
    
    val feeds = getFeeds(Providers.bookWalker) ++ getFeeds(Providers.paburi) ++ getFeeds(Providers.eBookJapan)
    
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

  get("/books/:oid") {
  val oid = new ObjectId(params("oid"))
    val book = BookDao.findOneByID(oid) match {case Some(x) => x; case _ => null}
      jade("book", "book" -> book)
  }
  
  get("/api/books") {
      contentType = "text/javascript"
    "{a:'b'}"
  }

  notFound {
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }
}
