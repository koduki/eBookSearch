organization := "cn.orz.pascal"

name := "eBookSearch"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.1"

seq(webSettings :_*)

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % "2.0.1",
  "org.scalatra" %% "scalatra-scalate" % "2.0.1",
  "org.eclipse.jetty" % "jetty-webapp" % "8.0.1.v20110908" % "container",
  "javax.servlet" % "servlet-api" % "2.5" % "provided",
  "org.clapper" % "sbt-markdown-plugin" % "0.3.1"
)

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-core" % "0.9.30",
  "ch.qos.logback" % "logback-classic" % "0.9.30",
  "org.slf4j" % "slf4j-simple" % "1.6.2"
)


libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" % "test"

libraryDependencies += "cn.orz.pascal" %% "mechanize" % "0.2"

libraryDependencies += "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT" 

libraryDependencies += "org.quartz-scheduler" % "quartz" % "2.0.2"

//libraryDependencies += "se.fishtank" %% "css-selectors-scala" % "0.1.1"

libraryDependencies += "cn.orz.pascal" %% "css-selectors-scala-uo" % "0.1.1"

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Sonatype OSS Public" at "http://oss.sonatype.org/content/groups/public/"

resolvers += "repo.novus rels" at "http://repo.novus.com/releases/"

resolvers += "repo.novus snaps" at "http://repo.novus.com/snapshots/"

resolvers += "Local Maven Repository" at "file:///" + System.getProperty("user.home") + "/.m2/repository/" 
