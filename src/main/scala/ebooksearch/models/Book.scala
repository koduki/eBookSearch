package cn.orz.pascal.scala.ebooksearch.models

// vim: set ts=2 sw=2 et:
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection

case class Book(asin:String,title:String,author:String, publisher:String,image_url:String, items:Set[Item])
object BookDao extends SalatDAO[Book, Int](collection = MongoConnection()("test")("books"))

