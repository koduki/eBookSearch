package cn.orz.pascal.scala.ebooksearch.web

import org.scalatra._
import java.net.URL
import scalate.ScalateSupport
import com.mongodb.casbah.commons._
import cn.orz.pascal.scala.ebooksearch.models._

class Application extends ScalatraServlet with ScalateSupport {
  beforeAll {
    contentType = "text/html"
  }

  get("/") {
    val feeds = FeedItemDao.find(MongoDBObject())
                           .sort(orderBy = MongoDBObject("createdAt" -> -1))  
                           .toList
    jade("index", "feeds" -> feeds)
  }

  get("/search") {
    val query = params('q)

    import cn.orz.pascal.scala.ebooksearch.searcher._
    val agent = new EBookJapanSearcher()
    val items = agent.search(query)

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
