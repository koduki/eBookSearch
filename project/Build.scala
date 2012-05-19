import sbt._
import sbt.Keys._
import sbt.IO._

object ProjectBuild extends Build {
  val hello = TaskKey[Unit]("hello", "Prints 'Hello World'")
  val helloTask = hello := {
    println("Hello World")
    (1 to 10).map(println)
  }

  val prod = TaskKey[Unit]("env-production", "change prodction enviroment.")
  val envTask = prod := {
    val dir = "src/main/webapp/WEB-INF/"
    val file = "web.xml"
    val env = "prod"
    println("change enviroment:" + env)
    copyFile(new File(dir + file + "." + env), new File(dir + file))
  }


  lazy val root = Project( id = "", base = file("."), 
   settings = Defaults.defaultSettings ++ Seq(helloTask, envTask)).dependsOn(
    uri("git://github.com/koduki/mechanize.git"),
    uri("git://github.com/koduki/css-selectors-scala.git")
  )
}
