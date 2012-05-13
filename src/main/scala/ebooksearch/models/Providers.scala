package cn.orz.pascal.scala.ebooksearch.models

object Providers {
  val items = Map(
    "eBookJapan" -> Provider("eBookJapan", "http://www.ebookjapan.jp/"),
    "Paburi" -> Provider("Paburi", "http://www.paburi.com/paburi/"),
    "BOOK☆WALKER" -> Provider("BOOK☆WALKER", "http://bookwalker.jp/"),
    "Jコミ" -> Provider("Jコミ", "http://www.j-comi.jp/"))

  def eBookJapan = items("eBookJapan")
  def paburi = items("Paburi")
  def bookWalker = items("BOOK☆WALKER")
  def jcomi = items("Jコミ")

  def apply(name: String) = items(name)
}
