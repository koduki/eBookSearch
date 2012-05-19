package cn.orz.pascal.ebooksearch.web
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.FileInputStream
import java.io._

class ResourceServlet extends HttpServlet {
  val mimeTypes = Map(
    "jpg" -> "image/jpeg",
    "png" -> "image/png",
    "ico" -> "image/vnd.microsoft.icon",
    "txt" -> "text/plain",
    "css" -> "text/css",
    "less" -> "text/less",
    "js" -> "text/javascript")
  override def doGet(req: HttpServletRequest, res: HttpServletResponse) {
    val path = getServletContext().getRealPath(req.getRequestURI())

    val file = new File(path)

    if (file.exists()) {
      val ext = file.getName().replaceAll(""".*\.""", "").toLowerCase()
      val mimeType = if (mimeTypes.contains(ext)) {
        mimeTypes(ext)
      } else {
        mimeTypes("txt")
      }
      res.setContentType(mimeType)

      val in = new FileInputStream(file)
      val out = res.getOutputStream()
      val buffer: Array[Byte] = new Array[Byte](1024)
      var numRead: Int = 0
      Iterator.continually(in.read(buffer)).takeWhile(_ != -1).foreach(n => out.write(buffer, 0, n))
    } else {
      res.sendError(404)
    }
  }

}