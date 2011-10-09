package cn.orz.pascal.scala.ebooksearch.web

import org.scalatra._
import java.net.URL
import scalate.ScalateSupport

class Application extends ScalatraServlet with ScalateSupport {

  get("/") {
    contentType = "text/html"
    layoutTemplate("index")
  }

  get("search") {
    val query = params('q)

    import cn.orz.pascal.scala.ebooksearch.searcher._
    //val agent = new EBookJapanSearcher()
    //val items = agent.search(query)


    contentType = "text/html"
  //layoutTemplate("search", ("items" -> 1),( "a" ->  3))
//        templateAttributes("foo") = "from template attributes"

        jade("search", "foo" -> "Configurable")

  }

  notFound {
    // Try to render a ScalateTemplate if no route matched
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound() 
  }
}
