package cn.orz.pascal.scala.commons.utils
// vim: set ts=2 sw=2 et:

import ch.qos.logback._
import org.slf4j._

trait LoggingSupport {
  val logger = LoggerFactory.getLogger(this.getClass)
  private def toString2(message:Any):String = if (message == null){ "" } else { message.toString }

  def debug(message:String) = logger.debug(message)
  def debug(message:Any) = logger.debug(toString2(message))
  def info(message:String) = logger.info(message)
  def info(message:Any) = logger.info(toString2(message))
  def warn(message:String) = logger.warn(message)
  def warn(message:Any) = logger.warn(toString2(message))
  def error(message:String) = logger.error(message)
  def error(message:Any) = logger.error(toString2(message))
}
