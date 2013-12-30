import sbt._
import com.typesafe.sbt.packager.Keys._
import sbt.Keys._
import com.typesafe.sbt.SbtNativePackager._

object Packaging {

  val settings: Seq[Setting[_]] = packagerSettings ++ deploymentSettings ++ Seq(
	name in Rpm := "helloworld",
	version in Rpm := "" + new java.util.Date().getTime,
	packageSummary in Linux := "Helloworld Package",
	rpmRelease := "1",
	rpmVendor := "Frank Ittermann",
	rpmRequirements ++= Seq("chkconfig", "java-1.7.0-openjdk-devel >= 1:1.7", "apache-tomcat >= 7.0"),
	rpmPost := Option("""service tomcat stop
service tomcat start"""),
	rpmGroup := Some("Applications/Internet"),
	rpmLicense := Some("BSD"),
	linuxPackageMappings <+= (target) map { bd =>
          println("target " + bd)
	  (packageMapping((bd / "scala-2.10/sbt-rpm_2.10-1.0.war") -> "/usr/share/tomcat/webapps/helloworld.war")
	   withUser "root" withGroup "root" withPerms "0755")
	}
  )
}
