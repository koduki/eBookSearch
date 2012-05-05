// vim: set ts=4 sw=4 et:
package cn.orz.pascal.scala.commons.utils
import com.twitter.util.Eval

object ConfigReader {
  def apply[T](name:String) = {
    val path = Thread.currentThread().getContextClassLoader().getResource(name).getPath
    val file = new java.io.File(path)
    Eval[T](file)
  }
}

