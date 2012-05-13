package cn.orz.pascal.scala.ebooksearch.web

import org.scalatra._
import java.net.URL
import scalate.ScalateSupport

import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.commons.utils.LoggingSupport
import cn.orz.pascal.scala.commons.utils.DateUtils._
import cn.orz.pascal.scala.commons.utils.ConfigReader
import cn.orz.pascal.scala.commons.utils.LevenshteinDistance
import cn.orz.pascal.scala.commons.utils.Serializer
import cn.orz.pascal.scala.ebooksearch.agent._
import cn.orz.pascal.scala.ebooksearch.config._
import ch.qos.logback._
import org.slf4j._
import scala.actors.Futures._
import com.mongodb.casbah.Imports._
import com.novus.salat.global._
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._

class WebFront extends BasicServlet {
  val config = ConfigReader[MyConfig]("config.scala")

      def getFeeds(provider: Provider, size:Int) = {
      val selecter = new BookSelecter(config)
      FeedItemDao
        .find((MongoDBObject("_id.provider" -> grater[Provider].asDBObject(provider))))
        .sort(orderBy = MongoDBObject("createdAt" -> -1))
        .limit(size)
        .toList
        .foldLeft(Map[(Provider, java.util.Date), List[Item]]()) { (r, x) =>
          val createdAt = dateTrim(x.createdAt)
          val list = if (r.contains((provider, createdAt))) { r(provider, createdAt) } else { List[Item]() }
          r + ((provider, createdAt) -> (list ++ List(x.item)))
        }.map { x =>
          x._1 -> x._2.map(item => selecter.select(item)).toSet
        }
    }
  
  get("/") {
    val feeds = getFeeds(Providers.bookWalker, 8) ++ getFeeds(Providers.paburi, 8) ++ getFeeds(Providers.eBookJapan, 8)
    val bookCount = BookDao.count()

    jade("index", "feeds" -> feeds, "bookCount" -> bookCount)
  }

  get("/news/:provider_name") {
    val provider = Providers(params("provider_name"))
    val feeds = getFeeds(provider, 100)
    val bookCount = BookDao.count()

    jade("index", "feeds" -> feeds, "bookCount" -> bookCount)
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
    val selecter = new BookSelecter(config)
    val books = items.map { item => selecter.select(item) }.toSet

    val hasNexts = results.map { x => x._2 }
    hasNextBKW = hasNexts(0)
    hasNextPBR = hasNexts(1)
    hasNextEBJ = hasNexts(2)

    QueryLogDao.insert(QueryLog(query, items, new java.util.Date()))

    jade("search",
      "books" -> books,
      "pageNumber" -> pageNumber,
      "nextBkw" -> (if (hasNextBKW) { 1 } else { 0 }),
      "nextEbj" -> (if (hasNextEBJ) { 1 } else { 0 }),
      "nextPbr" -> (if (hasNextPBR) { 1 } else { 0 }))
  }

  post("/books/change") {
    val oid = new ObjectId(params("oid"))
    val isbn = params("isbn").replaceAll("-", "")

    val title = params("title")
    val url = params("url")
    val value = params("value").toInt
    val author = params("author")
    val authorUrl = params("author_url")
    val imageUrl = params("image_url")
    val providerName = params("provider_name")

    val item = Item(title, url, value, author, authorUrl, imageUrl, Providers(providerName))

    val book = BookDao.findOneByID(oid) match { case Some(x) => x; case _ => null }
    val selecter = new BookSelecter(config)

    val newBook = selecter.change(book, item, isbn)
    redirect("/books/" + newBook.id)
  }

  get("/books/:oid") {
    val oid = new ObjectId(params("oid"))
    val book = BookDao.findOneByID(oid) match { case Some(x) => x; case _ => null }

    if (book == null) {
      resourceNotFound()
    } else {
      jade("book", "book" -> book)
    }
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
