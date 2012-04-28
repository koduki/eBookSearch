package cn.orz.pascal.scala.ebooksearch.models

// vim: set ts=2 sw=2 et:
case class Provider(name: String, url: String)
case class Item(title: String, url: String, value: Int, author: String, author_url: String, image_url: String, provider: Provider)
