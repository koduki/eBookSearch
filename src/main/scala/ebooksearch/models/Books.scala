package cn.orz.pascal.ebooksearch.models

import cn.orz.pascal.ebooksearch.models._
import cn.orz.pascal.ebooksearch.agent._
import cn.orz.pascal.ebooksearch.config.MyConfig
import cn.orz.pascal.commons.rakuten.RakutenBooks
import cn.orz.pascal.commons.rakuten.RakutenItem
import cn.orz.pascal.commons.utils.LoggingSupport
import cn.orz.pascal.commons.utils.ConfigReader
import cn.orz.pascal.commons.utils.LevenshteinDistance
import cn.orz.pascal.commons.utils.DateUtils._
import com.mongodb.casbah.Imports._
import com.novus.salat.global._
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._

object Books {
  def apply(config: MyConfig): Books = {
    new Books(config)
  }
}
class Books(val config: MyConfig) extends LoggingSupport {
  def change(source: Option[Book], item: Item, isbn: String): Option[Book] = {
    source match {
      case Some(sourceBook) => {

        info("isbn is %s".format(isbn))
        if (isbn.isEmpty()) {
          return None
        }

        val another = BookDao.find(MongoDBObject("isbn" -> isbn)).toList
        val book = if (another.isEmpty) {
          val rbs = new RakutenBooks(config.rakuten.developerId)
          val results = rbs.search(isbn)
          if (results.isEmpty) {
            return None
          }
          buildBook(item, results.first)
        } else {
          another.first
        }
        BookDao.save(book.addItem(item))
        BookDao.save(sourceBook.removeItem(item))

        info("%s change to %s from %s .".format(item.title + ":" + item.provider.name, book.id, sourceBook.id))
        Some(book)
      }
      case None => None
    }
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

  def getFeeds(provider: Provider, size: Int) = {
    val selecter = new Books(config)
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
      }.toList.sort((x, y) => x._1._2 > y._1._2)
  }

  private def selectFromRakuten(item: Item): Book = {
    val rbs = new RakutenBooks(config.rakuten.developerId)
    val title = item.title.replaceAll("【立ち読み版】", " ").replaceAll("【立ち読み版】", "")
    val author = item.author
      .replaceAll("\r\n", "")
      .replaceAll("\n", "")
      .replaceAll("著者：", "")
      .replaceAll("イラスト：.*", "")
      .replaceAll("漫画：", "")
      .replaceAll("原作：", "")
      .replaceAll("作画：.*", "")
      .replaceAll("／.*", "")
      .replaceAll("　", " ")
      .split(" ").first

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
        itemCaption = "",
        image = Image(item.image_url, item.image_url, item.image_url, item.image_url, item.image_url),
        isbn = "",
        items = Set(item))

    } else {
      val result = selectBestFitBook(item, results)
      val books = BookDao.find(MongoDBObject("isbn" -> result.isbn)).toList
      if (books.isEmpty) {
        debug("create new book [%s].".format(title))
        buildBook(item, result)
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

  private def buildBook(item: Item, result: RakutenItem): cn.orz.pascal.ebooksearch.models.Book = {
    Book(
      title = result.title,
      author = result.author,
      seriesName = result.seriesName,
      publisherName = result.publisherName,
      genre = result.size,
      salesDate = result.salesDate,
      itemCaption = result.itemCaption,
      image = Image(result.image.small, result.image.medium, result.image.large, result.image.veryLarge, result.image.original),
      isbn = result.isbn,
      items = Set(item))
  }
}
