// vim: set ts=4 sw=4 et:
package cn.orz.pascal.ebooksearch.config

trait MyConfig {
  case class AmazonWebServiceConfig(accessKeyId: String, secretKey: String, associateTag: String)
  val amazon: AmazonWebServiceConfig
  case class RakutenWebServiceConfig(developerId: String)
  val rakuten: RakutenWebServiceConfig
}
