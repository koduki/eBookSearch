// vim: set ts=4 sw=4 et:
package cn.orz.pascal.commons.aws
import scala.io.Source
import scala.xml.XML

class AmazonWebService(awsAccessKeyId: String, awsSecretKey: String, associateTag: String) {
  val signedRequestsHelper = new SignedRequestsHelper()
  case class Image(val small:String, val medium:String, val large:String)
  case class Item(val title: String, val author: String, val manufacturer: String, val detailUrl:String, val image: Image, val asin: String)
  def searchItem(keyword: String):List[Item] = {
    val params = Map(
      "Version" -> "2009-07-01",
      "Operation" -> "ItemSearch",
      "SearchIndex" -> "Books",
      "ResponseGroup" -> "Images,Small",
      "Keywords" -> keyword,
      "AssociateTag" -> associateTag,
      "Service" -> "AWSECommerceService")

    val queryUrl = signedRequestsHelper.sign(awsAccessKeyId, awsSecretKey)(params)
    val xml = readXml(queryUrl)
    (xml \\ "Item").map{node => parse(node)}.toList

  }

  protected def readXml(queryUrl: String): scala.xml.Elem = {
    val source = Source.fromURL(queryUrl, "UTF-8")
    val xml = XML.loadString(source.mkString)
    xml
  }

  def parse(node: scala.xml.Node) = {
    val asin = node \ "ASIN" text
    val author = node \ "ItemAttributes" \ "Author" text
    val manufacturer = node \ "ItemAttributes" \ "Manufacturer" text
    val title = node \ "ItemAttributes" \ "Title" text
    val url = node \ "DetailPageURL" text
    val small = node \ "SmallImage" \ "URL" text
    val medium = node \ "MediumImage" \ "URL" text
    val large = node \ "LargeImage" \ "URL" text

    Item(title, author, manufacturer, url, Image(small, medium, large), asin)
  }

}
