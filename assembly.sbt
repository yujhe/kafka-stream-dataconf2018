runMain in Compile := Defaults.runMainTask(fullClasspath in Compile, runner in(Compile, run))

run in Compile := Defaults.runTask(fullClasspath in Compile, mainClass in(Compile, run), runner in(Compile, run))

assembleArtifact in assemblyPackageScala := true

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

test in assembly := {} // skip test in assembly
