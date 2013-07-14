sbt package-war rpm:package-bin
cp target/rpm/RPMS/noarch/*.rpm yum-repo/
createrepo yum-repo/
