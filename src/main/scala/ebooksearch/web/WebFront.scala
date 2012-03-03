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

class WebFront extends ScalatraServlet with ScalateSupport with LoggingSupport {
  beforeAll {
    contentType = "text/html"
  }

  get("/") {
    val feeds = FeedItemDao.find(MongoDBObject("createdAt" -> MongoDBObject("$gte" -> today)))
    .sort(orderBy = MongoDBObject("createdAt" -> -1))  
                           .toList
    jade("index", "feeds" -> feeds)
  }

  get("/search") {
    val query = params('q)

    
    val ebookjapan = new EBookJapanAgent search(query)
    val nicoseiga  = new NicoSeigaAgent search(query)
    val bookwalker = new BookWalkerAgent search(query)
    val paburi = new PaburiAgent search(query)
    val items = paburi ++ bookwalker ++ nicoseiga ++ ebookjapan

    QueryLogDao.insert(QueryLog(query, items, new java.util.Date()))

    jade("search", "items" -> items)
  }

  notFound {
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound() 
  }
}
