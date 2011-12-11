package cn.orz.pascal.scala.ebooksearch.searchagent
import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.ebooksearch.utils.LoggingSupport

// vim: set ts=2 sw=2 et:
class NicoSeigaSearchAgent extends SearchAgent with LoggingSupport {
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

        def get(pageNum:Int) = {
            val page  = agent.get("http://www.ebookjapan.jp/ebj/newlist.asp?genre_request=0&page=" + pageNum.toString)
            val main_line = page.get(Id("main_line"))
            val item_nodes = (main_line \\ "li").filter(item => (item \ "@class" text) == "heightLineChangeable")

            parse(item_nodes)
        }

        (1 to 5).map(get(_)).toList.flatten
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
        
        val text = ""//((page.get(Id("main_area_all"))\\("ul")).attr("@class", "bk_pagenation") \ "li").text
        val pageCount = (((page.get(Id("main_area_all"))\\("ul")).attr("@class", "bk_pagenation"))(0)\("li")).size - 2

        debug("url:%s, keyword:%s, encode:%s, text:%s, count:%d".format(page.url, keyword, encode(keyword), text, pageCount).replaceAll("\n", ""))
       
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
