package cn.orz.pascal.scala.commons.utils

import scala.xml.Node

object XmlUtils {
  implicit def node2richNode(nodes: List[Node]) = {
    new RichNodeList(nodes)
  }

  class RichNodeList(nodes: List[Node]) {
    def attr(attribute: String) = {
      val attrs = if (nodes.isEmpty) {
        None
      } else {
        nodes.first.attribute(attribute)
      }

      (attrs match {
        case Some(x) => x.text
        case None => ""
      })
    }
  }

}