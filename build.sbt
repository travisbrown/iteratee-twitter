import ReleaseTransformations._

ThisBuild / organization := "io.iteratee"
ThisBuild / crossScalaVersions := Seq("2.12.14", "2.13.6")
ThisBuild / githubWorkflowJavaVersions := Seq("adopt@1.8")
ThisBuild / githubWorkflowPublishTargetBranches := Nil
ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(
    List("clean", "coverage", "test", "coverageReport", "scalastyle", "scalafmtCheckAll"),
    id = None,
    name = Some("Test")
  ),
  WorkflowStep.Use(
    UseRef.Public(
      "codecov",
      "codecov-action",
      "v1"
    )
  )
)

val compilerOptions = Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture"
)

val iterateeVersion = "0.20.0"
val catbirdVersion = "21.2.0"
val disciplineVersion = "0.9.0"

val scalaCheckVersion = "1.15.4"
val scalaTestVersion = "3.2.9"

val baseSettings = Seq(
  scalacOptions ++= (compilerOptions :+ "-Yno-predef" :+ "-Ywarn-unused-import"),
  Compile / console / scalacOptions ++= compilerOptions,
  Compile / test / scalacOptions ++= (compilerOptions :+ "-Ywarn-unused-import"),
  coverageHighlighting := true,
  Compile / scalastyleSources ++= (Compile / sourceDirectories).value,
  addCompilerPlugin(("org.typelevel" % "kind-projector" % "0.13.0").cross(CrossVersion.full))
)

lazy val allSettings = baseSettings ++ publishSettings

lazy val docSettings = Seq(
  SiteScaladoc / siteSubdirName := "api",
  Compile / doc / scalacOptions ++= Seq(
    "-groups",
    "-implicits",
    "-doc-source-url",
    scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
    "-sourcepath",
    (LocalRootProject / baseDirectory).value.getAbsolutePath
  ),
  git.remoteRepo := "git@github.com:travisbrown/iteratee-twitter.git"
)

lazy val twitter = project
  .in(file("."))
  .enablePlugins(GhpagesPlugin, SiteScaladocPlugin)
  .configs(IntegrationTest)
  .settings(allSettings ++ Defaults.itSettings)
  .settings(docSettings)
  .settings(
    moduleName := "iteratee-twitter",
    libraryDependencies ++= Seq(
      "io.iteratee" %% "iteratee-files" % iterateeVersion,
      "io.iteratee" %% "iteratee-testing" % iterateeVersion % Test,
      "io.catbird" %% "catbird-effect" % catbirdVersion,
      "io.catbird" %% "catbird-util" % catbirdVersion
    )
  )

lazy val publishSettings = Seq(
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  homepage := Some(url("https://github.com/travisbrown/iteratee-twitter")),
  licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  publishMavenStyle := true,
  Test / publishArtifact := false,
  pomIncludeRepository := { _ =>
    false
  },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots".at(nexus + "content/repositories/snapshots"))
    else
      Some("releases".at(nexus + "service/local/staging/deploy/maven2"))
  },
  autoAPIMappings := true,
  apiURL := Some(url("https://travisbrown.github.io/iteratee-twitter/api/")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/travisbrown/iteratee-twitter"),
      "scm:git:git@github.com:travisbrown/iteratee-twitter.git"
    )
  ),
  pomExtra := (
    <developers>
      <developer>
        <id>travisbrown</id>
        <name>Travis Brown</name>
        <url>https://twitter.com/travisbrown</url>
      </developer>
    </developers>
  )
)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

credentials ++= (
  for {
    username <- Option(System.getenv().get("SONATYPE_USERNAME"))
    password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
  } yield Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    username,
    password
  )
).toSeq
