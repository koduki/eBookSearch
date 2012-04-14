import sbt._
import sbt.Keys._

object ProjectBuild extends Build {
    lazy val root = Project( id = "root", base = file(".")).dependsOn(
        uri("git://github.com/koduki/mechanize.git"),
        uri("git://github.com/koduki/css-selectors-scala.git")
    )
}
