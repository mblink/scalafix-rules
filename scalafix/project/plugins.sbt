addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.34")
addSbtPlugin("com.eed3si9n" % "sbt-projectmatrix" % "0.9.0")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.22")

resolvers += "bondlink-maven-repo" at "https://raw.githubusercontent.com/mblink/maven-repo/main"
addSbtPlugin("bondlink" % "sbt-git-publish" % "0.0.5")
