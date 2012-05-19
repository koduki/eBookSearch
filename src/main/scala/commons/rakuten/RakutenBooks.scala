// vim: set ts=4 sw=4 et:
package cn.orz.pascal.commons.rakuten
import scala.xml.XML
import java.net.URL
import cn.orz.pascal.commons.utils.NetUtils._

case class Image(val small: String, val medium: String, val large: String, val veryLarge: String, val original: String)
case class RakutenItem(
  val isbn: String,
  val title: String,
  val author: String,
  val seriesName: String,
  val publisherName: String,
  val size: String,
  val salesDate: String,
  val itemCaption: String,
  val image: Image)

class RakutenBooks(val developerId: String) {
  def search(title: String, author: String): List[RakutenItem] = {
    val queryUrl = "http://api.rakuten.co.jp/rws/3.0/rest?developerId=" + developerId + "&operation=BooksBookSearch&version=2011-12-01&title=" + utf8(title) + "&author=" + utf8(author)
    searchByQuery(queryUrl)
  }

  def search(isbn: String): List[RakutenItem] = {
    val queryUrl = "http://api.rakuten.co.jp/rws/3.0/rest?developerId=" + developerId + "&operation=BooksBookSearch&version=2011-12-01&isbn=" + isbn
    searchByQuery(queryUrl)
  }

  private def searchByQuery(queryUrl: String): List[RakutenItem] = {
    val items = XML.load(new URL(queryUrl)) \\ "Item"

    items.map { item =>
      val title = item \ "title" text
      val author = item \ "author" text
      val isbn = item \ "isbn" text
      val seriesName = item \ "seriesName" text
      val publisherName = item \ "publisherName" text
      val size = item \ "size" text
      val salesDate = item \ "salesDate" text
      val itemCaption = item \ "itemCaption" text

      val baseImageUrl = (item \ "largeImageUrl" text).replaceAll("""\?_ex=.*""", "")
      val image = Image(
        item \ "smallImageUrl" text,
        item \ "mediumImageUrl" text,
        item \ "largeImageUrl" text,
        baseImageUrl + "?_ext=260x260",
        baseImageUrl)

      RakutenItem(isbn, title, author, seriesName, publisherName, size, salesDate, itemCaption, image)
    }.toList
  }
}
