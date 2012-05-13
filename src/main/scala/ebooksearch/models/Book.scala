package cn.orz.pascal.scala.ebooksearch.models

// vim: set ts=2 sw=2 et:
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection

case class Image(val small: String, val medium: String, val large: String)
case class Book(@Key("_id") id: ObjectId = new ObjectId, isbn: String, title: String, author: String, publisher: String, image: Image, items: Set[Item]) {
  def addItem(item: Item) = {
    Book(this.id, this.isbn, this.title, this.author, this.publisher, this.image, this.items + item)
  }

  def removeItem(item: Item) = {
    Book(this.id, this.isbn, this.title, this.author, this.publisher, this.image, this.items - item)
  }
}

object BookDao extends SalatDAO[Book, ObjectId](collection = MongoConnection()("test")("books"))
