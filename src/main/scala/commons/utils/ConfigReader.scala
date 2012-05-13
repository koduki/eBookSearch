// vim: set ts=4 sw=4 et:
package cn.orz.pascal.scala.commons.utils
import com.twitter.util.Eval

object ConfigReader {
  val pool = scala.collection.mutable.Map[String, Any]()

  def apply[T](name: String): T = {
    if (pool.contains(name)) {
      pool(name).asInstanceOf[T]
    } else {
      val path = Thread.currentThread().getContextClassLoader().getResource(name).getPath
      val file = new java.io.FileInputStream(path)
      val config = try Eval[T](file)
      finally file.close
      pool(name) = config
      config.asInstanceOf[T]
    }
  }
}

