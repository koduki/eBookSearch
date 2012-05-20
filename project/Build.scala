import sbt._
import sbt.Keys._
import sbt.IO._

object ProjectBuild extends Build {
  val dependencyFiles = Seq("src/main/webapp/WEB-INF/web.xml")


  val enviroment = InputKey[Unit]("enviroment", "chagen enviroment seting.")
  val enviromentTask = enviroment <<= inputTask { (argTask: TaskKey[Seq[String]]) =>
    argTask map { (args: Seq[String]) => 
        changeEnviroment(args.first)
    }
  }

  def changeEnviroment(env:String) = {
    println("change enviroment:" + env)
    dependencyFiles.foreach { path =>
      val envPath = path + "." + env
      val envFile = new File(envPath)
      val msg = if (envFile.exists) {
        copyFile(envFile, new File(path))
        "copy:" + envPath
      } else {
        "not found:" + envPath
      }
      println(msg)
    }
  }

  lazy val root = Project( id = "", base = file("."), 
   settings = Defaults.defaultSettings ++ Seq(enviromentTask)).dependsOn(
    uri("git://github.com/koduki/mechanize.git"),
    uri("git://github.com/koduki/css-selectors-scala.git")
  )
}
