resolvers += "Web plugin repo" at "http://siasia.github.com/maven2"

resolvers += Classpaths.typesafeResolver

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.8"))

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse" % "1.4.0")

