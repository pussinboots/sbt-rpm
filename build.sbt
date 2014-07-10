
name :="sbt-rpm"

scalaVersion :="2.10.2"

version :="1.0"

seq(webSettings :_*)

libraryDependencies ++= Seq(
    "org.eclipse.jetty" % "jetty-webapp" % "8.1.7.v20120910" % "container",
    "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container,compile" artifacts Artifact("javax.servlet", "jar", "jar")
)

Packaging.settings
