package cn.orz.pascal.scala.ebooksearch.models

import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.ebooksearch.agent._
import cn.orz.pascal.scala.ebooksearch.config.MyConfig
import cn.orz.pascal.commons.rakuten.RakutenBooks
import cn.orz.pascal.commons.rakuten.RakutenItem
import cn.orz.pascal.scala.commons.utils.LoggingSupport
import cn.orz.pascal.scala.commons.utils.ConfigReader
import cn.orz.pascal.scala.commons.utils.LevenshteinDistance
import com.mongodb.casbah.Imports._
import com.novus.salat.global._
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._

class BookSelecter(val config:MyConfig) extends LoggingSupport {
  def change(source:Book, item: Item, isbn:String): Book = {
    BookDao.save(source.removeItem(item))

    val another =  BookDao.find(MongoDBObject("isbn" -> isbn )).toList 
    val book = if (another.isEmpty) {
      val rbs = new RakutenBooks(config.rakuten.developerId)
      val results = rbs.search(isbn) 
      if(results.isEmpty) {
        throw new Exception("not found book") 
      }
      val result = results.first
      Book(
          title = result.title, 
          author = result.author, 
          seriesName = result.seriesName, 
          publisherName = result.publisherName, 
          genre = result.size, 
          salesDate = result.salesDate, 
          image = Image(result.image.small, result.image.medium, result.image.large, result.image.veryLarge, result.image.original), 
          isbn = result.isbn, 
          items = Set(item))
    } else {
      another.first
    }
    BookDao.save(book.addItem(item))
    info("%s change to %s from %s .".format(item.title + ":" + item.provider.name, book.id, source.id))
    book
  }

  def select(item: Item): Book = {
    val books = BookDao.find(MongoDBObject("items" -> grater[Item].asDBObject(item))) toList
    val result = if (books.isEmpty) {
      debug("%s is return new item.".format(item.title))
      val book = selectFromRakuten(item)
      BookDao.save(book)
      book
      
    } else {
      debug("%s is return db item.".format(item.title))
      books.first
    }
    result
  }

  private def selectFromRakuten(item: Item): Book = {
    val rbs = new RakutenBooks(config.rakuten.developerId)
    val title = item.title.replaceAll("【立ち読み版】", " ").replaceAll("【立ち読み版】", "")
    val author = item.author.replaceAll("著者：", "").replaceAll("イラスト：.*", "").replaceAll("漫画：", "").replaceAll("原作：", "").replaceAll("作画：.*", "")

    debug("title=%s,\tauthor=%s".format(title, author))
    val results = rbs.search(title, author)

    if (results.isEmpty) {
      info("%s is not found.".format(title))
      Book(
          title = item.title, 
          author = item.author, 
          seriesName = "", 
          publisherName = "", 
          genre = "", 
          salesDate = "", 
          image = Image(item.image_url, item.image_url, item.image_url, item.image_url, item.image_url), 
          isbn = "", 
          items = Set(item))
 
    } else {
      val result = selectBestFitBook(item, results)
      val books = BookDao.find(MongoDBObject("isbn" -> result.isbn)).toList
      if (books.isEmpty) {
        debug("create new book [%s].".format(title))
        Book(
          title = result.title, 
          author = result.author, 
          seriesName = result.seriesName, 
          publisherName = result.publisherName, 
          genre = result.size, 
          salesDate = result.salesDate, 
          image = Image(result.image.small, result.image.medium, result.image.large, result.image.veryLarge, result.image.original), 
          isbn = result.isbn, 
          items = Set(item))
 
      } else {
        debug("update book [%s].".format(title))
        books.first.addItem(item)
      }
    }
  }

  private def selectBestFitBook(item: Item, results: List[RakutenItem]): RakutenItem = {
    val result = results.map(x => (x -> LevenshteinDistance(trim(item.title), trim(x.title)))).sort((x, y) => x._2 < y._2).first._1
    result
  }

  private def trim(str: String): String = {
    import com.ibm.icu.text.Transliterator
    val transliterator = Transliterator.getInstance("Fullwidth-Halfwidth")
    transliterator.transliterate(str)
  }
}
