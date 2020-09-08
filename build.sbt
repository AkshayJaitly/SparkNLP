name := "nlp"

version := "0.1"

scalaVersion := "2.11.12"

assemblyJarName in assembly := "scalaNlp-1.0.jar"



libraryDependencies += "com.johnsnowlabs.nlp" %% "spark-nlp" % "2.5.5"

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.5"


// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.5"

// https://mvnrepository.com/artifact/org.apache.spark/spark-mllib
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.4.5"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.11"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

