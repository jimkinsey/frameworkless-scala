name := "todo-mvp"

scalaVersion := "2.12.7"

enablePlugins(JavaAppPackaging)

libraryDependencies ++= {
  val http4sVersion = "0.18.17"
  Seq(
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-server-metrics" % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "io.frees" %% "frees-http4s" % "0.8.0",
    "com.lihaoyi" %% "scalatags" % "0.6.7",
    "com.lihaoyi" %% "utest" % "0.6.3" % "test",
    "org.jsoup" % "jsoup" % "1.11.3" % "test"
  )
}

testFrameworks += new TestFramework("utest.runner.Framework")
