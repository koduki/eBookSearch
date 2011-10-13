package cn.orz.pascal.scala.ebooksearch.utils
// vim: set ts=2 sw=2 et:

import ch.qos.logback._
import org.slf4j._

trait LoggingSupport {
  val logger = LoggerFactory.getLogger(this.getClass)

  def debug(message:String) = logger.debug(message)
  def debug(message:Any) = logger.debug(message.toString)
  def info(message:String) = logger.info(message)
  def info(message:Any) = logger.info(message.toString)
  def warn(message:String) = logger.warn(message)
  def warn(message:Any) = logger.warn(message.toString)
  def error(message:String) = logger.error(message)
  def error(message:Any) = logger.error(message.toString)
}
