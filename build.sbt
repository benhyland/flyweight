name := "flyweight"

scalaVersion := "2.10.2"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

fork in Test := true

// include the jar as a local lib because it needs to be named just so to be able to instrument JDK classes loaded by the boot classloader.
javaOptions in Test += "-javaagent:lib/allocation.jar"
