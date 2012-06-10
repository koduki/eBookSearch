package cn.orz.pascal.commons.utils

object ISBN {
  def to13(isbn10: String): String = {
    val isbn12 = ("978" + isbn10.take(9))
    val xs = isbn12.toList.map(x => x.toString.toInt)

    val sum = (0 to xs.size - 1).
      map { i => if (i % 2 == 0) xs(i) * 1 else xs(i) * 3 }.
      foldLeft(0) { (r, x) => r + x }
    val tailNum = sum.toString.last.toString.toInt
    val checkDegit = 10 - tailNum

    isbn12 + checkDegit
  }
}