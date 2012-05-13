// vim: set ts=2 sw=2 et:
package cn.orz.pascal.scala.commons.utils

object NetUtils {
  def utf8(text: String) = java.net.URLEncoder.encode(text, "UTF-8")
  def sjis(text: String) = java.net.URLEncoder.encode(text, "Shift_JIS")
}
