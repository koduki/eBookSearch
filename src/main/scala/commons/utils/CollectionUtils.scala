package cn.orz.pascal.commons.utils

object CollectionUtils {
  def splitGroup[T](n: Int)(xs: List[T], r: List[List[T]] = List()): List[List[T]] = {
    xs match {
      case List() => r
      case _ => splitGroup(n)(xs.drop(n), r ++ List(xs.take(n)))
    }
  }
}