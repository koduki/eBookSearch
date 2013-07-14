package cn.orz.pascal.ebooksearch.agent
import cn.orz.pascal.ebooksearch.models._
import cn.orz.pascal.commons.utils.XmlUtils._
import se.fishtank.css.selectors.Selectors._
import cn.orz.pascal.mechanize._
import scala.xml.NodeSeq
import scala.xml.Node
import org.scala_tools.time.Imports._

// vim: set ts=2 sw=2 et:
class KoboAgent extends SimpleAgent {
  override def provider = Providers.kobo
  override def getNewItems(): List[Item] = {
    val date = DateTime.yesterday.toString("yyyy-MM-dd")
    getNewItems(date)
  }
  def getNewItems(date: String): List[Item] = {
    val agent = new Mechanize()
    agent.isJavaScriptEnabled_=(false)

    val url = "http://wakufactory.jp/kobo/new.php?d=" + date
    val page = agent.get(url)
    val urls = page.get(Class("nb")) \\ "td" \ "a" \\ "@href" map { x => x.text }

    urls.map { url =>
      println("url:%s".format(url))

      try {
        val page = agent.get(url)
        val node = page.get(Class("SCContentFull"))

        val title = (node $ ".KV2ItemVitals h1" text).trim
        val author = (node $ "#h4Author ul li a" text).trim
        val author_url = (node $ "#h4Author ul li a") \ "@href" text
        val value = try {
          (node $ ".KV2ItemDetails .KV2OurPrice strong" text).replaceAll("円", "").trim.toInt
        } catch {
          case e: NumberFormatException => {
            //warn(e)
            0
          }
        }
        val image_url = (node $ ".KV2ItemThumbImg img.KV2ItemThumb") \ "@src" text

        Item(title, url, value, author, author_url, image_url, provider)
      } catch {
        case e: Exception => {
          e.printStackTrace();
          null;
        }
      }
    }.toList.filter(_ != null)
  }

  override protected def read(keyword: String, pageNumber: Int): Option[NodeSeq] = {
    val agent = new Mechanize()
    agent.isJavaScriptEnabled_=(false)
    val queryUrl = "http://rakuten.kobobooks.com/search/search.html?q=" + utf8(keyword) + "&t=&f=keyword&p=" + pageNumber + "&s=&g=both&l="
    debug("url:%s, keyword:%s, encode:%s".format(queryUrl, keyword, sjis(keyword)).replaceAll("\n", ""))

    val page = agent.get(queryUrl)
    val nodes = page.asXml $ "li.SCSearchItem"

    if (!nodes.isEmpty) {
      val iconNext = page.get(Class("iconNext"))
      this._hasNext = (iconNext != null)
      Some(nodes)
    } else {
      None
    }
  }

  override protected def parse(nodes: NodeSeq): List[Item] = {
    nodes.map { node =>
      val title = node \\ "h3" \ "@title" text
      val url = "http://rakuten.kobobooks.com" + ((node \\ "h3" \ "a").last \ "@href" text).replaceAll("""\?s=.*"""", "")
      val author = (node $ ".Author>span>a" text).trim
      val author_url = "http://rakuten.kobobooks.com" + ((node $ ".Author>span>a").first \ "@href" text)
      val value = try {
        (node $ ".KV2OurPrice strong" text).replaceAll(",", "").replaceAll(" ", "").replaceAll("円", "").trim.toInt
      } catch {
        case e: NumberFormatException => {
          warn(e)
          0
        }
      }
      val image_url = (node \\ "img" \ "@src" text)
      Item(title, url, value, author, author_url, image_url, provider)
    }.toList
  }

}
