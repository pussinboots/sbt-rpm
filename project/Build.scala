import sbt._
import Keys._
import com.typesafe.startscript.StartScriptPlugin
import sbtassembly.Plugin._

object BuildSettings {
    import Dependencies._
    import Resolvers._

    val buildOrganization = "org.frank"
    val buildVersion = "1.0"
    val buildScalaVersion = "2.9.1"

    classpathTypes ~= (_ + "orbit")

    val globalSettings = Seq(
        organization := buildOrganization,
        version := buildVersion,
        scalaVersion := buildScalaVersion,
        scalacOptions += "-deprecation",
        fork in test := true,
        libraryDependencies ++= Seq(slf4jSimpleTest, scalatest, jettyServerTest, jettyServletOrbit, /*scalamock, easymock,*/ jmock),
        resolvers := Seq(jbossRepo, akkaRepo, sonatypeRepo, gephiRepo, typeSafeRepo, mavenLocal))

    val projectSettings = Defaults.defaultSettings ++ globalSettings
}

object Resolvers {
    val sonatypeRepo = "Sonatype Release" at "http://oss.sonatype.org/content/repositories/releases"
    val jbossRepo = "JBoss" at "http://repository.jboss.org/nexus/content/groups/public/"
    val akkaRepo = "Akka" at "http://repo.akka.io/repository/"
    val gephiRepo = "Gephi" at "http://nexus.gephi.org/nexus/content/repositories/releases"

    val typeSafeRepo = "Typesafe" at "http://repo.typesafe.com/typesafe/releases/"

    //central maven repository missing workaround for own dependency management
    val mavenLocal= "Local Maven" at "file:///"+Path.userHome+"/.m2/repository"
}

object Dependencies {
    val logback = "ch.qos.logback" % "logback-classic" % "1.0.9"
    val scalatest = "org.scalatest" %% "scalatest" % "1.6.1" % "test"
    val scalamock = "org.scalamock" %% "scalamock-scalatest-support" % "latest.integration" % "test"
    val easymock = "org.easymock" % "easymock" % "3.0" % "test"
    val jmock = "org.jmock" % "jmock-legacy" % "2.6.0" % "test"

    val slf4jSimple = "org.slf4j" % "slf4j-simple" % "1.6.2"
    val slf4jSimpleTest = slf4jSimple % "test"

    val jettyVersion = "8.1.7.v20120910"//"7.4.0.v20110414"
    val jettyServer = "org.eclipse.jetty" % "jetty-server" % jettyVersion
    
    val jettyServlet = "org.eclipse.jetty" % "jetty-servlet" % jettyVersion % "test"
    val jettyServletOrbit = "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" artifacts (Artifact("javax.servlet", "jar", "jar"))

    val jettyServerTest = jettyServer % "test"

    val akka = "se.scalablesolutions.akka" % "akka-actor" % "1.3.1"
    val akkaHttp = "se.scalablesolutions.akka" % "akka-http" % "1.3.1"
    val akkaAmqp = "se.scalablesolutions.akka" % "akka-amqp" % "1.3.1"

    val asyncHttp = "com.ning" % "async-http-client" % "1.6.5"

    val jsoup = "org.jsoup" % "jsoup" % "1.6.1"

    val casbahCore = "com.mongodb.casbah" %% "casbah-core" % "2.1.5-1"

    val htmlUnit = "net.sourceforge.htmlunit" 			  % "htmlunit" 		% "2.11" /* excludeAll(
												    ExclusionRule(organization = "xalan")
												 )*/

    val guava = "com.google.guava"                    % "guava"            		% "11.0.2"

    val stax2 = "org.codehaus.woodstox"               % "stax2-api"      		% "3.1.1"
    val woodstox = "org.codehaus.woodstox"               % "woodstox-core-asl" 	% "4.0.6"

    //val gephitoolkit = "org.gephi" % "gephi-toolkit" % "0.8.2"
    val grapht = "net.sf.jgrapht" % "jgrapht" % "0.8.3"

    val mysql = "mysql" % "mysql-connector-java" % "5.1.18"

    val anorm = "play" %% "anorm" % "2.0.4"

    val scalikejdbc = "com.github.seratch" %% "scalikejdbc" % "1.5.3"

    val xstream = "xstream" % "xstream" % "1.2.2"

    val crawlercommons = "com.google.code.crawler-commons" % "crawler-commons" % "0.2"

    val awsJavaSdk = "com.amazonaws" % "aws-java-sdk" % "1.4.0.1"
}

object WebWordsBuild extends Build {
    import BuildSettings._
    import Dependencies._
    import Resolvers._
    import AssemblyKeys._
    import com.typesafe.sbt.packager.Keys._
    import com.typesafe.sbt.SbtNativePackager._

    //seq(assemblySettings: _*)

    override lazy val settings = super.settings ++ BuildSettings.globalSettings

    lazy val root = Project("webwords",
                            file("."),
                            settings = BuildSettings.projectSettings ++
                            Seq(
                                StartScriptPlugin.stage in Compile := Unit
                            )) aggregate(common, web, indexer)

    lazy val web = Project("webwords-web",
                           file("web"),
                           settings = BuildSettings.projectSettings ++ net.virtualvoid.sbt.graph.Plugin.graphSettings++
                           StartScriptPlugin.startScriptForClassesSettings ++
                           Seq(libraryDependencies ++= Seq(akkaHttp, jettyServer, jettyServlet))) dependsOn(common % "compile->compile;test->test")

    lazy val indexer = Project("webwords-indexer",
                              file("indexer"),
                              settings = BuildSettings.projectSettings ++ net.virtualvoid.sbt.graph.Plugin.graphSettings ++ assemblySettings ++
Seq( 
    assembleArtifact in packageScala := true
)++
packagerSettings ++ deploymentSettings ++ Seq(
	name in Rpm := "indexer",
	version in Rpm := "" + new java.util.Date().getTime,
	packageSummary in Rpm := "Crawler Package",
	rpmRelease := "1",
	rpmVendor := "ingenious",
	rpmRequirements ++= Seq("chkconfig", "java-1.7.0-openjdk-devel >= 1:1.7", "rabbitmq-server >= 3.0", "epel-release >= 5", "mongo-10gen-server >= 2", "mysql-server >= 5"),

	rpmPost := Option("""chkconfig mongod on
chkconfig rabbitmq-server on
chkconfig indexer on
service mongod start
service rabbitmq-server start
service indexer stop
service indexer start"""),

	rpmLicense := Some("BSD"),
	linuxPackageMappings <+= (target) map { bd =>
          println("target " + bd)
	  (packageMapping((bd / "webwords-indexer-assembly-1.0.jar") -> "/usr/share/indexer/indexer.jar")
	   withUser "root" withGroup "root" withPerms "0755")
	},
	linuxPackageMappings <+= (baseDirectory) map { bd =>
          println(bd)
	  (packageMapping((bd / "rpm/indexer.sh") -> "/usr/share/indexer/indexer.sh")
	   withUser "root" withGroup "root" withPerms "0755")
	},
	linuxPackageMappings <+= (baseDirectory) map { bd =>
          println(bd)
	  (packageMapping((bd / "rpm/indexer") -> "/etc/rc.d/init.d/indexer")
	   withUser "root" withGroup "root" withPerms "0755")
	},
	linuxPackageMappings <+= (baseDirectory) map { bd =>
          println(bd)
	  (packageMapping((bd / "rpm/indexer.logrotate.sh") -> "/etc/logrotate.de/indexer.sh")
	   withUser "root" withGroup "root" withPerms "0644")
	},
        linuxPackageMappings <+= (baseDirectory) map { bd =>
          println(bd)
	  (packageMapping((bd / "rpm/zanox.js") -> "/usr/share/indexer/zanox.js")
	   withUser "root" withGroup "root" withPerms "0755")
	},
	linuxPackageMappings <+= (baseDirectory) map { bd =>
          println(bd)
	  (packageMapping((bd / "rpm/setenv.sh") -> "/usr/share/indexer/setenv.sh")
	   withUser "root" withGroup "root" withPerms "0755")
	},
	linuxPackageMappings <+= (baseDirectory) map { bd =>
          println(bd)
	  (packageMapping((bd / "rpm/graphs.txt") -> "/usr/share/indexer/graphs/graphs.txt")
	   withUser "root" withGroup "root" withPerms "0755")
	}
) ++
                              StartScriptPlugin.startScriptForClassesSettings ++ 
			      Seq(Keys.mainClass in Compile := Option("com.typesafe.webwords.indexer.Main")) ++
                              Seq(libraryDependencies ++= Seq(jsoup, guava, stax2, woodstox, grapht, scalikejdbc, anorm, mysql, xstream/*, awsJavaSdk*/))) dependsOn(common % "compile->compile;test->test") settings (
 mergeStrategy in assembly := { 
        case PathList("META-INF", xs @ _*) =>
	    (xs map {_.toLowerCase}) match {
              case (Seq("eclipsef.rsa")) | (Seq	("eclipsef.sf")) => MergeStrategy.discard
	      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) => MergeStrategy.discard
	      case _ => MergeStrategy.concat
	    }
        case _ => MergeStrategy.first 
      },
 test in assembly := {},
 mainClass in assembly := Some("com.typesafe.webwords.indexer.Main")
)

    lazy val common = Project("webwords-common",
                           file("common"),
                           settings = BuildSettings.projectSettings ++ net.virtualvoid.sbt.graph.Plugin.graphSettings ++
                           Seq(libraryDependencies ++= Seq(akka, akkaAmqp, asyncHttp, casbahCore, htmlUnit, crawlercommons, logback)))
}
