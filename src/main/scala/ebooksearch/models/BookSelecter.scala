package cn.orz.pascal.scala.ebooksearch.models

import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.commons.utils.LoggingSupport
import cn.orz.pascal.scala.commons.utils.ConfigReader
import cn.orz.pascal.commons.aws.AmazonWebService
import cn.orz.pascal.commons.aws.{ Item => AmazonItem }
import cn.orz.pascal.scala.commons.utils.LevenshteinDistance
import cn.orz.pascal.scala.ebooksearch.agent._
import ch.qos.logback._
import org.slf4j._
import scala.actors.Futures._
import com.mongodb.casbah.Imports._
import com.novus.salat.global._
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._

object BookSelecter {

  def select(aws: AmazonWebService, item: Item): Book = {
    val dbResult = BookDao.find(MongoDBObject("items" -> grater[Item].asDBObject(item))) toList
    val keyword = (item.title + item.author).replaceAll("【立ち読み版】", " ").replaceAll("著者：", " ").replaceAll("イラスト：", " ")
    if (!dbResult.isEmpty) {
      dbResult.first
    } else {
      println(keyword)
      val book = selectFromAWS(aws, item, keyword)
      BookDao.save(book)
      println(book)
      book
    }
  }

  private def selectFromAWS(aws: AmazonWebService, item: Item, keyword: String): Book = {
    val results = aws.searchItem(keyword)
    if (results.isEmpty) {
      println("Blank!")
      Book(title = item.title, author = item.author, publisher = "", image = Image(item.image_url, item.image_url, item.image_url), asin = "", items = Set(item))
    } else {
      println(results)
      val result = selectBestFitBook(item, results)
      val books = BookDao.find(MongoDBObject("asin" -> result.asin)).toList
      if (books.isEmpty) {
        Book(title = result.title, author = result.author, publisher = result.manufacturer, image = Image(result.image.small, result.image.medium, result.image.large), asin = result.asin, items = Set(item))
      } else {
        books.first.addItem(item)
      }
    }
  }

  private def selectBestFitBook(item: Item, results: List[AmazonItem]): AmazonItem = {
//    val result = results.map(x => (x -> LevenshteinDistance(trim(item.title), trim(x.title)))).sort((x, y) => x._2 < y._2).first._1
    val result = results.first
    result
  }

  private def trim(str: String): String = {
    import com.ibm.icu.text.Transliterator
    val transliterator = Transliterator.getInstance("Fullwidth-Halfwidth")
    transliterator.transliterate(str).replaceAll(" ", "")
  }
}