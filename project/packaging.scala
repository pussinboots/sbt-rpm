import sbt._
import com.typesafe.sbt.packager.Keys._
import sbt.Keys._
import com.typesafe.sbt.SbtNativePackager._

object Packaging {

  val settings: Seq[Setting[_]] = packagerSettings ++ deploymentSettings ++ Seq(
	name in Rpm := "indexer",
	version in Rpm := "" + new java.util.Date().getTime,
	packageSummary in Linux := "Crawler Package",
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
	  (packageMapping((bd / "scala-2.10/sbt-rpm_2.10-1.0.war") -> "/usr/share/indexer/helloworld.war")
	   withUser "root" withGroup "root" withPerms "0755")
	}
)
}