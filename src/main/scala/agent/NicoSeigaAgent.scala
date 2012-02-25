package cn.orz.pascal.scala.ebooksearch.agent
import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.ebooksearch.utils.LoggingSupport

// vim: set ts=2 sw=2 et:
class NicoSeigaAgent extends Agent with LoggingSupport {
    val provider = Provider("ニコニコ静画(電子書籍)", "http://seiga.nicovideo.jp/book/?track=global_navi_top")
    implicit def nodeSeq2richNodeSeq(nodes:scala.xml.NodeSeq) = {
      new {
        def attr(name:String, value:String) = nodes.filter(node => (node \ name).text == value)
      } 
    }

    def search(keyword:String):List[Item] = {
        parse(read(keyword))
    }

    def getNewBooks():List[Item] = {
        import cn.orz.pascal.scala.mechanize._
        val agent = new Mechanize()
        val baseUrl = "http://seiga.nicovideo.jp"

        def getItems(page:HtmlPage) = {
          (page.get(Id("bk_article"))\\"div").attr("@class", "bk_list").map(item => ((item\\"@href").text, (item\\("@src")).text))
        }

        def getItemInfo(item:(String, String)) = {
          val page = agent.get(baseUrl + item._1)
              
          val title = (page.get(Id("bk_article"))\"div"\"h2").text.trim
          val author = (page.get(Class("bk_author"))\"a").text.trim
          val author_url = (page.get(Class("bk_author"))\"a"\"@href").text.trim
          val value = ((page.get(Id("bk_book_info_details"))\"ul")(1)\"li")(3).text.trim.replaceFirst("値段: ", "").replaceFirst("円", "").toInt
              
          val updateAt = (page.get(Id("bk_article"))\"div"\"div")(0).text.trim.split(" ")(0)

          (updateAt, Item(title, baseUrl + item._1, value, author, author_url, item._2, provider))
        }
            
        def get(updateAt:String, count:Int):List[Item] = {
          val page = agent.get("http://seiga.nicovideo.jp/book/list?sort=f&order=d&page=" + count)

          val items = getItems(page)
          val result = items.map(item => getItemInfo(item))

          val feedCount = result.filter(item => item._1 == updateAt).size
          
          debug("page:%d, updateAt:%s, result_count:%d, feed_count:%d".format(count, updateAt, feedCount, result.size))

          if (feedCount == result.size) {
            result.map(item => item._2).toList ++ get(updateAt, count + 1)
          } else {
            result.map(item => item._2).toList
          }
        }

        val list_page = agent.get("http://seiga.nicovideo.jp/book/list?sort=f&order=d&page=" + 1)
        val item = getItems(list_page)(0)
        val info_page = agent.get(baseUrl + item._1)
        val updateAt = (info_page.get(Id("bk_article"))\"div"\"div")(0).text.trim.split(" ")(0)

        get(updateAt, 1)
    }

    def read(keyword:String):scala.xml.NodeSeq = {
        import cn.orz.pascal.scala.mechanize._

        def encode(keyword:String) = java.net.URLEncoder.encode(keyword, "UTF-8")
        def readPages(agent:Mechanize, url:String, pageCount:Int) = {
            val blank = <blank /> \ "any"

            (1 to pageCount).map{ i =>
                val page = agent.get(url + "&page=" + i)
                (page.get(Id("bk_article"))\\"li").attr("@class", "bk_book_entry")
            }.foldLeft(blank)((r, node) => r ++ node)
        }

        val agent = new Mechanize()
        val queryUrl = "http://seiga.nicovideo.jp/search/" + encode(keyword) + "?target=book&track=seiga_book_keyword";
        val page = agent.get(queryUrl)
        
        val pager =(page.get(Id("main_area_all"))\\("ul")).attr("@class", "bk_pagenation")
        val pageCount = if (pager.size != 0){ (pager(0)\("li")).size - 2 } else { -1 }

        debug("url:%s, keyword:%s, encode:%s, count:%d".format(page.url, keyword, encode(keyword), pageCount).replaceAll("\n", ""))
       
        readPages(agent, queryUrl, pageCount)
    }

    def parse(item_nodes:scala.xml.NodeSeq):List[Item] = {
        val baseUrl = "http://seiga.nicovideo.jp";
        item_nodes.map( item =>
            Item(
                ((item \\ "li").attr("@class", "bk_title") \ "a").text.trim,
                baseUrl + ((item \\ "li").attr("@class", "bk_title") \ "a" \ "@href").text,
                -1,
                ((item \\ "li").attr("@class", "bk_author") \ "a")(0).text.trim,
                baseUrl + (((item \\ "li").attr("@class", "bk_author") \ "a")(0)\"@href").text,
                baseUrl + ((item \\ "div").attr("@class", "bk_list") \\ "@src").text,
                provider
             )
        ).toList
    }
}
