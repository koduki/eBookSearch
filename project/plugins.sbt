resolvers += "Web plugin repo" at "http://siasia.github.com/maven2"

resolvers += Classpaths.typesafeResolver

addSbtPlugin("com.github.siasia" %% "xsbt-web-plugin" % "0.1.2") 

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse" % "1.4.0")
