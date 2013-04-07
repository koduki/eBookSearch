package cn.orz.pascal.ebooksearch.web

import org.scalatra._
import java.net.URL
import scalate.ScalateSupport
import com.mongodb.casbah.commons._
import cn.orz.pascal.ebooksearch.models._
import cn.orz.pascal.commons.utils.LoggingSupport
import cn.orz.pascal.commons.utils.DateUtils._
import cn.orz.pascal.ebooksearch.agent._
import ch.qos.logback._
import org.slf4j._
import cn.orz.pascal.ebooksearch.batch.NewItemCrawler

trait BasicServlet extends ScalatraServlet with ScalateSupport with LoggingSupport {
  def validateParam(name: String, default: Boolean): Boolean = {
    if (params.contains(name) && (params(name).matches("[01]"))) {
      params(name) == "1"
    } else {
      default
    }
  }

  def validateParam(name: String, default: Int): Int = {
    if (params.contains(name) && params(name).matches("""\d""")) {
      params(name).toInt
    } else {
      default
    }
  }

  beforeAll {
    contentType = "text/html"
  }

  override def get(routeMatchers: RouteMatcher*)(action: ⇒ Any): Route = {
    val result = super.get(routeMatchers: _*) {
      val start = java.lang.System.currentTimeMillis()
      action
      val end = java.lang.System.currentTimeMillis()
      info("response time\t%s\t%d(ms)".format(routeMatchers.toString, end - start))
    }

    result
  }

  override def resourceNotFound(): Any = {
    if (isDevelopmentMode) {
      super.resourceNotFound();
    } else {
      response.setStatus(404)
      servletContext.getRequestDispatcher("/error/404.html").forward(request, response)
    }
  }

  get("/rpc/execute_batch/:name") {
    val name = params("name")
    loggingTime[String]("EVENT:START_JOB\tNAME:clawling") {
      try {
        name match {
          case "kobo_crawler" => (new NewItemCrawler).getKobo
          case "bookwalker_crawler" => (new NewItemCrawler).getBookWlaker
          case "ebookjapan_crawler" => (new NewItemCrawler).getEBookJapan
          case "paburi_crawler" => (new NewItemCrawler).getPaburi
          case "cleanUp" => (new Books(null)).cleanUp
        }
      } catch {
        case e: Exception =>
          e.printStackTrace();
      }
      () => "{status:success}"

    }("EVENT:END_JOB\tNAME:clawling\tPROC_TIME:%time (ms)")
  }
}
