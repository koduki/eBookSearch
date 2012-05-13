// vim: set ts=4 sw=4 et:
package cn.orz.pascal.commons.rakuten
import scala.xml.XML
import java.net.URL
import cn.orz.pascal.scala.commons.utils.NetUtils._

case class Image(val small: String, val medium: String, val large: String)
case class RakutenItem(val isbn:String, val title: String, val author: String, val manufacturer: String,val image: Image)

class RakutenBooks(val developerId:String) {
  def search(title:String, author:String):List[RakutenItem] = {
    val queryUrl = "http://api.rakuten.co.jp/rws/3.0/rest?developerId="+ developerId +"&operation=BooksBookSearch&version=2011-12-01&title=" + utf8(title) +"&author=" + utf8(author)
    searchByQuery(queryUrl)
  }

  def search(isbn:String):List[RakutenItem] = {
    val queryUrl = "http://api.rakuten.co.jp/rws/3.0/rest?developerId="+ developerId +"&operation=BooksBookSearch&version=2011-12-01&isbn=" + isbn
    searchByQuery(queryUrl)
  }

  private def searchByQuery(queryUrl:String):List[RakutenItem] = {
    val items = XML.load(new URL(queryUrl)) \\ "Item"
  
    items.map{ item => 
      val title = item \ "title" text
      val author  = item \ "author" text
      val isbn = item \ "isbn" text
      val manufacturer  = (item \ "seriesName" text) + " - " + (item \ "publisherName" text)

      val image =Image(item \ "smallImageUrl" text, item \ "mediumImageUrl" text, item \ "largeImageUrl" text)
      RakutenItem(isbn, title, author, manufacturer, image)
    }.toList
  }
}
