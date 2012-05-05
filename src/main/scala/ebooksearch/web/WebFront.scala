package cn.orz.pascal.scala.ebooksearch.web

import org.scalatra._
import java.net.URL
import scalate.ScalateSupport

import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.commons.utils.LoggingSupport
import cn.orz.pascal.scala.commons.utils.DateUtils._
import cn.orz.pascal.scala.commons.utils.ConfigReader
import cn.orz.pascal.commons.aws.AmazonWebService
import cn.orz.pascal.scala.commons.utils.LevenshteinDistance
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
def search(aws:AmazonWebService, item:Item):Book = {
  val keyword = (item.title + item.author).replaceAll("【立ち読み版】", " ").replaceAll("著者：", " ").replaceAll("イラスト：", " ")
  println(keyword)
  val results = aws.searchItem(keyword)
  val book=if (results.isEmpty) {
    println("Blank!")
    Book(title = item.title, author= item.author, publisher="", image=Image(item.image_url, item.image_url, item.image_url), asin="", items=Set(item))
  } else {
    println(results)
    val result =  results.map(x => (x -> LevenshteinDistance(trim(item.title), trim(x.title)))).sort((x, y) => x._2 < y._2).first._1
    val books = BookDao.find(MongoDBObject("asin" -> result.asin )) toList
    val b =if (books.isEmpty){
        Book(title = result.title, author= result.author, publisher=result.manufacturer, image=Image(result.image.small, result.image.medium, result.image.large), asin=result.asin, items=Set(item))
    }else{
        books.first.addItem(item)
    }
    b
  }
  BookDao.save(book)
  println(book)
  book
}

def trim(str:String) = {
                                                                                                        import com.ibm.icu.text.Transliterator
                                                                                                        val transliterator = Transliterator.getInstance("Fullwidth-Halfwidth")
                                                                                                          transliterator.transliterate(str).replaceAll(" ", "")
                                                                                                      }
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
    
    val aws = new AmazonWebService(config.amazon.accessKeyId, config.amazon.secretKey, config.amazon.associateTag)
    val items = results.map { x => x._1 }.fold(List[Item]()) { (r, item) => r ++ item }
    val books = items.map{ item => search(aws, item) }

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
