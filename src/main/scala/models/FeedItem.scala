package cn.orz.pascal.scala.ebooksearch.models

// vim: set ts=2 sw=2 et:
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection

case class FeedItem(@Key("_id") item:Item, createdAt:java.util.Date)
object FeedItemDao extends SalatDAO[FeedItem, Int](collection = MongoConnection()("test")("feed_items"))

