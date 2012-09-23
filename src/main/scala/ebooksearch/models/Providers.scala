package cn.orz.pascal.ebooksearch.models

object Providers {
  val items = Map(
    "eBookJapan" -> Provider("eBookJapan", "http://www.ebookjapan.jp/"),
    "Paburi" -> Provider("Paburi", "http://www.paburi.com/paburi/"),
    "BOOK☆WALKER" -> Provider("BOOK☆WALKER", "http://bookwalker.jp/"),
    "Kobo" -> Provider("電子ブック楽天＜kobo＞", "http://rakuten.kobobooks.com/"))

  def eBookJapan = items("eBookJapan")
  def paburi = items("Paburi")
  def bookWalker = items("BOOK☆WALKER")
  def kobo = items("Kobo")
  
  def apply(name: String) = items(name)
}
