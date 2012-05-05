// vim: set ts=4 sw=4 et:
package cn.orz.pascal.scala.ebooksearch.config

trait MyConfig {
  case class AmazonPAAConfig(accessKeyId:String, secretKey:String, associateTag:String)
  val amazonPAA:AmazonPAAConfig
}
