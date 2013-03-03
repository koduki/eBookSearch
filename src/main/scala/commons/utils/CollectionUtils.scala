package cn.orz.pascal.commons.utils

object CollectionUtils {
  def splitGroup[T](n: Int)(xs: Traversable[T], r: Traversable[Traversable[T]] = Traversable()): Traversable[Traversable[T]] = {
    xs match {
      case List() => r
      case _ => splitGroup(n)(xs.drop(n), r ++ Traversable(xs.take(n)))
    }
  }
}