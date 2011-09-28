package cn.orz.pascal.scala.ebooksearch.searcher

// vim: set ts=4 sw=4 et:
case class Provider(name:String, url:String)
case class Item(title:String, url:String, value:Int, author:String, author_url:String, image_url:String, provider:Provider)

trait Searcher { def search(keyword:String):List[Item] }
class EBookJapanSearcher extends Searcher {
    val provider = Provider("eBookJapan", "http://www.ebookjapan.jp/")

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

        def encode(keyword:String) = java.net.URLEncoder.encode(keyword, "SJIS")
        def readPages(agent:Mechanize, url:String, pageCount:Int) = {
            val blank = <blank /> \ "any"

            (1 to pageCount).map{ i =>
                val page = agent.get(url + "&page=" + i)
                page.get(Id("main_line"))\\"li"
            }.foldLeft(blank)((r, node) => r ++ node)
        }

        val agent = new Mechanize()
        val queryUrl = "http://www.ebookjapan.jp/ebj/search.asp?s=6&sd=0&ebj_desc=on&q=" + encode(keyword)
        val page = agent.get(queryUrl)
        
        val text = page.get(Class("pagenavi")).text
        val pageCount = "(全)(.*?)(ページ)".r.findFirstMatchIn(text).get.group(2).toInt

        println("----------------------")
        println(page.url)
        println(keyword)
        println(encode(keyword))
        println(text)
        println(pageCount)
        println("----------------------")
       
        readPages(agent, queryUrl, pageCount)
    }

    def parse(item_nodes:scala.xml.NodeSeq):List[Item] = {
        item_nodes.map( item =>
            Item(
                (item \ "h5" \ "a").text.trim,
                "http://www.ebookjapan.jp" + (item \ "h5" \ "a" \ "@href").text,
                (item \ "h6")(0).child(0).text.trim.replaceAll("円.*", "").toInt,
                ((item\"div")(1)\"a").text.trim,
                "http://www.ebookjapan.jp" + ((item\"div")(1)\"a"\"@href").text,
                (item \\ "img" \ "@src").text,
                provider
             )
        ).toList
    }

}
