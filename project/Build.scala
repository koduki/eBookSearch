import sbt._
import sbt.Keys._
import sbt.IO._

object ProjectBuild extends Build {
  val demo = InputKey[Unit]("demo")
  lazy val ExtraProps = config("extra-props") extend(Compile)
  val demoTask = demo in ExtraProps <<= inputTask { (argTask: TaskKey[Seq[String]]) =>
    // Here, we map the argument task `argTask`
    // and a normal setting `scalaVersion`
    (argTask, scalaVersion) map { (args: Seq[String], sv: String) =>
        println("The current Scala version is " + sv)
        println("The arguments to demo were:")
        args foreach println
     }
   }
  val prod = TaskKey[Unit]("env-production", "change prodction enviroment.")
  val dev = TaskKey[Unit]("env-development", "change development enviroment.")
  val envTaskProd = prod := envTask("prod")
  val envTaskDev  = dev := envTask("dev")

  def envTask(env:String) = {
    val dir = "src/main/webapp/WEB-INF/"
    val file = "web.xml"
    println("change enviroment:" + env)
    copyFile(new File(dir + file + "." + env), new File(dir + file))
  }

  lazy val root = Project( id = "", base = file("."), 
   settings = Defaults.defaultSettings ++ Seq(demoTask, envTaskProd, envTaskDev)).dependsOn(
    uri("git://github.com/koduki/mechanize.git"),
    uri("git://github.com/koduki/css-selectors-scala.git")
  )
}
