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

libraryDependencies += "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT" 

libraryDependencies += "org.quartz-scheduler" % "quartz" % "2.0.2"

libraryDependencies += "commons-codec" % "commons-codec" % "1.3"

libraryDependencies += "com.twitter" % "util-eval" % "1.12.13" withSources()

libraryDependencies += "com.ibm.icu" % "icu4j" % "49.1"

libraryDependencies += "net.debasishg" %% "sjson" % "0.17"

libraryDependencies += "org.scala-tools.time" % "time_2.9.1" % "0.5"

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Sonatype OSS Public" at "http://oss.sonatype.org/content/groups/public/"

resolvers += "repo.novus rels" at "http://repo.novus.com/releases/"

resolvers += "repo.novus snaps" at "http://repo.novus.com/snapshots/"

resolvers += "T repo" at "http://maven.twttr.com/"

