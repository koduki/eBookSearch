package cn.orz.pascal.ebooksearch.web

import org.scalatra._
import java.net.URL
import scalate.ScalateSupport
import cn.orz.pascal.ebooksearch.models._
import cn.orz.pascal.commons.utils.LoggingSupport
import cn.orz.pascal.commons.utils.DateUtils._
import cn.orz.pascal.commons.utils.ConfigReader
import cn.orz.pascal.commons.utils.LevenshteinDistance
import cn.orz.pascal.commons.utils.Serializer
import cn.orz.pascal.ebooksearch.agent._
import cn.orz.pascal.ebooksearch.config._
import ch.qos.logback._
import org.slf4j._
import scala.actors.Futures._
import com.mongodb.casbah.Imports._
import com.novus.salat.global._
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._
import cn.orz.pascal.ebooksearch.agent.KoboAgent
import cn.orz.pascal.ebooksearch.batch.NewItemCrawlerJob

class WebFront extends BasicServlet {
  val config = ConfigReader[MyConfig]("config.scala")

  get("/") {
    info("development mode is " + isDevelopmentMode)

    val feeds = Books(config).getFeeds(Providers.bookWalker, 8) ++
      Books(config).getFeeds(Providers.kobo, 8) ++
      Books(config).getFeeds(Providers.paburi, 8) ++
      Books(config).getFeeds(Providers.eBookJapan, 8)
    val bookCount = BookDao.count("isbn" $ne "")

    ssp("index", "feeds" -> feeds, "bookCount" -> bookCount, "title" -> "Top")
  }

  get("/news/:provider_name") {
    val provider = Providers(params("provider_name"))
    val feeds = Books(config).getFeeds(provider, 100)
    val bookCount = BookDao.count("isbn" $ne "")

    ssp("index", "feeds" -> feeds, "bookCount" -> bookCount, "title" -> ("新着:" + provider.name))
  }

  get("/search") {
    val query = params('q)
    val pageNumber = validateParam("page", 1)
    var hasNextBKW = validateParam("bkw", true)
    var hasNextPBR = validateParam("pbr", true)
    var hasNextEBJ = validateParam("ebj", true)
    var hasNextKBO = validateParam("kbo", true)
    val results = List(
      new BookWalkerAgent,
      new PaburiAgent,
      new EBookJapanAgent,
      new KoboAgent)
      .map(x => future { x.search(query, pageNumber) })
      .map(_())

    val items = results.map { x => x._1 }.fold(List[Item]()) { (r, item) => r ++ item }
    val selecter = new Books(config)
    val books = items.map { item => selecter.select(item) }
      .foldLeft(Map[com.mongodb.casbah.Imports.ObjectId, Book]()) { (r, x) => r + (x.id -> x) }
      .values.toList

    val hasNexts = results.map { x => x._2 }
    hasNextBKW = hasNexts(0)
    hasNextPBR = hasNexts(1)
    hasNextEBJ = hasNexts(2)
    hasNextKBO = hasNexts(3)

    QueryLogDao.insert(QueryLog(query, items, new java.util.Date()))

    ssp("search",
      "books" -> books,
      "title" -> "検索結果",
      "pageNumber" -> pageNumber,
      "nextBkw" -> (if (hasNextBKW) { 1 } else { 0 }),
      "nextEbj" -> (if (hasNextEBJ) { 1 } else { 0 }),
      "nextPbr" -> (if (hasNextPBR) { 1 } else { 0 }),
      "nextKbo" -> (if (hasNextKBO) { 1 } else { 0 }))
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

    val book = BookDao.findOneByID(oid)
    val selecter = new Books(config)

    selecter.change(book, item, isbn) match {
      case Some(newBook) => redirect("/books/" + newBook.id)
      case None => {
        session.setAttribute("message", "ISBN: " + isbn + " は見つかりませんでした.")
        redirect("/books/" + oid)
      }
    }

  }

  get("/books/:oid") {
    val oid = new ObjectId(params("oid"))
    debug("oid is " + oid)
    val book = BookDao.findOneByID(oid) match { case Some(x) => x; case _ => null }
    val message = if (session.contains("message")) {
      val msg = session("message")
      session.removeAttribute("message")
      debug("message is " + msg)
      msg
    } else {
      ""
    }

    if (book == null) {
      resourceNotFound()
    } else {
      ssp("book", "book" -> book, "title" -> book.title, "message" -> message)
    }
  }

  get("/api/books") {
    contentType = "text/javascript"
    "{a:'b'}"
  }
  
  get("/rpc/execute_batch"){
    (new NewItemCrawlerJob).execute
    "finish"
  }

  notFound {
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }
}
