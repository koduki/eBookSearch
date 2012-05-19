// vim: set ts=4 sw=4 et:
package cn.orz.pascal.commons.utils

object Serializer {
  import java.io._
  import sun.misc.BASE64Encoder
  import sun.misc.BASE64Decoder

  def serialize[T](obj: T): String = {
    val byteArray = new ByteArrayOutputStream();
    val data = try {
      val os = new ObjectOutputStream(byteArray);
      try {
        os.writeObject(obj)
      } finally {
        os.close
      }
      val bytes = byteArray.toByteArray
      (new BASE64Encoder()).encodeBuffer(bytes)
    } finally {
      byteArray.close
    }

    data
  }

  def unserialize[T](data: String): T = {
    val bytes = (new BASE64Decoder()).decodeBuffer(data);
    val is = new ObjectInputStream(new ByteArrayInputStream(bytes))
    try {
      is.readObject.asInstanceOf[T]
    } finally {
      is.close()
    }
  }
}
