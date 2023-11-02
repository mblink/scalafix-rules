addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.11.1")
addSbtPlugin("com.eed3si9n" % "sbt-projectmatrix" % "0.9.1")
addSbtPlugin("org.typelevel" % "sbt-tpolecat" % "0.5.0")

resolvers += "bondlink-maven-repo" at "https://raw.githubusercontent.com/mblink/maven-repo/main"
addSbtPlugin("bondlink" % "sbt-git-publish" % "0.0.5")
