package cn.orz.pascal.scala.ebooksearch.web

import org.scalatra._
import java.net.URL
import scalate.ScalateSupport
import com.mongodb.casbah.commons._
import cn.orz.pascal.scala.ebooksearch.models._
import cn.orz.pascal.scala.commons.utils.LoggingSupport
import cn.orz.pascal.scala.commons.utils.DateUtils._
import cn.orz.pascal.scala.ebooksearch.agent._
import ch.qos.logback._
import org.slf4j._

trait EBookSearchServlet extends ScalatraServlet with ScalateSupport with LoggingSupport {
  override def get(routeMatchers: RouteMatcher*)(action: ⇒ Any): Route = {

    val result = super.get(routeMatchers: _*) {
      val start = java.lang.System.currentTimeMillis()
      action
      val end = java.lang.System.currentTimeMillis()
      info("response time\t%s\t%d(ms)".format(routeMatchers.toString, end - start))
    }

    result
  }
}