Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val V = _root_.scalafix.sbt.BuildInfo

lazy val rulesCrossVersions = Seq(V.scala213, V.scala212)
lazy val scala3Version = "3.3.5"

inThisBuild(
  List(
    organization := "bondlink",
    version := "0.13.0",
    homepage := Some(url("https://github.com/mblink/scalafix-rules")),
    licenses := Seq(License.Apache2),
    publishTo := Some("BondLink S3".at("s3://bondlink-maven-repo")),
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
  )
)

lazy val noPublishSettings = Seq(
  publish / skip := true,
  publishLocal / skip := true,
)

lazy val bondlinkScalafix = project.in(file("."))
  .settings(noPublishSettings)
  .aggregate(
    rules.projectRefs ++
      input.projectRefs ++
      output.projectRefs ++
      tests.projectRefs: _*
  )

lazy val rules = projectMatrix
  .settings(
    moduleName := "scalafix-rules",
    libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion,
    scalacOptions ++= Seq("-Ypatmat-exhaust-depth", "off"),
  )
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(rulesCrossVersions)

lazy val input = projectMatrix
  .settings(noPublishSettings)
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(scalaVersions = rulesCrossVersions :+ scala3Version)

lazy val output = projectMatrix
  .settings(noPublishSettings)
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(scalaVersions = rulesCrossVersions :+ scala3Version)

lazy val testsAggregate = Project("tests", file("target/testsAggregate"))
  .settings(noPublishSettings)
  .aggregate(tests.projectRefs: _*)

lazy val tests = projectMatrix
  .settings(noPublishSettings)
  .settings(
    libraryDependencies += "ch.epfl.scala" % "scalafix-testkit" % V.scalafixVersion % Test cross CrossVersion.full,
    scalafixTestkitOutputSourceDirectories :=
      TargetAxis
        .resolve(output, Compile / unmanagedSourceDirectories)
        .value,
    scalafixTestkitInputSourceDirectories :=
      TargetAxis
        .resolve(input, Compile / unmanagedSourceDirectories)
        .value,
    scalafixTestkitInputClasspath :=
      TargetAxis.resolve(input, Compile / fullClasspath).value,
    scalafixTestkitInputScalacOptions :=
      TargetAxis.resolve(input, Compile / scalacOptions).value,
    scalafixTestkitInputScalaVersion :=
      TargetAxis.resolve(input, Compile / scalaVersion).value
  )
  .defaultAxes(
    rulesCrossVersions.map(VirtualAxis.scalaABIVersion) :+ VirtualAxis.jvm: _*
  )
  .customRow(
    scalaVersions = Seq(V.scala212),
    axisValues = Seq(TargetAxis(scala3Version), VirtualAxis.jvm),
    settings = Seq()
  )
  .customRow(
    scalaVersions = Seq(V.scala213),
    axisValues = Seq(TargetAxis(V.scala213), VirtualAxis.jvm),
    settings = Seq()
  )
  .customRow(
    scalaVersions = Seq(V.scala212),
    axisValues = Seq(TargetAxis(V.scala212), VirtualAxis.jvm),
    settings = Seq()
  )
  .dependsOn(rules)
  .enablePlugins(ScalafixTestkitPlugin)
