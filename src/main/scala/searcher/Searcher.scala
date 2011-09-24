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

    def read(keyword:String):scala.xml.NodeSeq = {
        import cn.orz.pascal.scala.mechanize._

        def encode(keyword:String) = java.net.URLEncoder.encode(keyword, "SJIS")

        val agent = new Mechanize()
        val page = agent.get("http://www.ebookjapan.jp/ebj/search.asp?q=" + encode(keyword) + "&ebj_desc=on")
        
        val text =page.get(Class("pagenavi"))
        val pageCount = "(全)(.*?)(ページ)".r.findFirstMatchIn(text).get.group(2).toInt

        page.get(Id("main_line"))\\"li"
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
