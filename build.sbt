name := "kafka-stream-dataconf"

version := "0.0.1"

scalaVersion := "2.12.6"

// XXX: workaround for [NOT FOUND] javax.ws.rs#javax.ws.rs-api;2.1!javax.ws.rs-api.${packaging.type}
// ref: https://github.com/bitrich-info/xchange-stream/issues/32
val workaround: Unit = {
  sys.props += "packaging.type" -> "jar"
  ()
}

libraryDependencies += "org.apache.kafka" % "kafka-streams" % "2.0.0"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.0.0"
libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "2.0.0"
