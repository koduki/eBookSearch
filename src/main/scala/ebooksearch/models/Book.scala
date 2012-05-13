package cn.orz.pascal.scala.ebooksearch.models

// vim: set ts=2 sw=2 et:
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection

case class Image(val small: String, val medium: String, val large: String, val veryLarge: String, val original: String)
case class Book(
  @Key("_id") id: ObjectId = new ObjectId,
  val isbn: String,
  val title: String,
  val author: String,
  val seriesName: String,
  val publisherName: String,
  val genre: String,
  val salesDate: String,
  val itemCaption: String,
  val image: Image,
  val items: Set[Item]) {

  def addItem(item: Item): Book = {
    Book(this.id, this.isbn, this.title, this.author, this.seriesName, this.publisherName, this.genre, this.salesDate, this.itemCaption, this.image, this.items + item)
  }

  def removeItem(item: Item): Book = {
    Book(this.id, this.isbn, this.title, this.author, this.seriesName, this.publisherName, this.genre, this.salesDate, this.itemCaption, this.image, this.items - item)
  }
}

object BookDao extends SalatDAO[Book, ObjectId](collection = MongoConnection()("test")("books"))
