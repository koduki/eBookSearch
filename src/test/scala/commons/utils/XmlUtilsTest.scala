package cn.orz.pascal.scala.commons.utils
// vim: set ts=4 sw=4 et:
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers

class XmlUtilsTest extends WordSpec with ShouldMatchers {
  import XmlUtils._
  "attr" when {
    "Empty List" should {
      val nodes = List()
      "return blank string." in {
        nodes.attr("src") should be("")
        nodes.attr("href") should be("")
      }
    }

    "found a attribute" should {
      val nodes = List(<a href='hoge.html'></a>)
      "return value." in {
        nodes.attr("href") should be("hoge.html")
      }
    }

    "found some attributes" should {
      val nodes = List(<img src='hoge1.jpg'/>, <img src='hoge2.png'/>)
      "return first value." in {
        nodes.attr("src") should be("hoge1.jpg")
      }
    }

    "not found  attribute" should {
      val nodes = List(<a href='hoge.html'></a>)
      "return balnk string." in {
        nodes.attr("src") should be("")
      }
    }

  }

}

