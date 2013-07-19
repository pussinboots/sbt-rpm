This is a simple helloworld web archive (war) project.

That illustrates the use of sbt native packager (https://github.com/sbt/sbt-native-packager) 
plugin to build a simple automated deployment with RPM and puppet.

The basic things like register yum repo and install the helloworld rpm
from that repo is done by puppet. The dependencies of the helloworld rpm
like java and tomcat are managed by the rpm specification in the project/packaging.scala
file with the sbt native package plugin. 

This is a minimal puppet approach the rpm dependencies could also be managed
by puppet and also configured by puppet.

Required:
 - git (`apt-get install git`)
 - createrepo (is needed to create a local yum repo -> install with `apt-get install createrepo`)
 - rpmbuild (is needed to build a rom with maven and deploy it to the local yum repo -> install with `apt-get install rpm`)
 - vagrant (optional to test the puppet deployment -> install with `apt-get install vagrant`)
 - virtualbox (is needed for vagrant as vm provider -> `apt-get install virtualbox`) 
 - puppet (is only needed in the vagrant box and is prepared in the configured base box)
 - CentOS64_201307 (self created base box at the moment have problems to publish it in git or else were hopefully is fixed in some days)

Start with clone this repository

Build the project.

1) install sbt (http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html) <br />
2) run sbt console with
   `sbt` <br />
3) run command to build war
   `package-war` <br />
4) run command to build rpm
   `rpm:package-bin` <br />
5) then copy the build rpm from target/rpm/RPMS/noarch/ to the yum-repo folder <br />
6) run command createrepo
   `createrepo yum-repo` <br />
7) then a new rpm version was build and can be deployed with puppet <br />

Automated deployment.

1) download base box for vagrant (could take some minutes the first time, if the download was 
   successfull it is stored local) do it only one time
   `vagrant box add CentOS64_201307 https://dl.dropboxusercontent.com/u/35824962/vagrant/CentOS64_201307` <br />
2) start vm with vagrant 
   `vagrant up` <br />
3) connect to the started vm with ssh (windows putty)
   `vagrant ssh` <br />
4) (ssh on helloworld machine)
   `wget localhost:8080/helloworld` <br />
   result should be
   `<html><body>Hello World from rpm package</body></html>` <br />
5) (ssh on apache machine)
   `wget localhost`
   result should be
   `<h1>Hello from a Vagrant VM</h1>` <br />
   

The helloworld vm will be setup with ip address 192.168.1.10 and the helloworld app can also be
requested with http://192.168.1.10:8080/helloworld from the host system.

The apache vm will be setup with ip address 192.168.1.11 and the index.html file can also be
requested with http://192.168.1.11 from the host system.

You can change the ip address in the Vagrant file see web_config.vm.network :hostonly, "192.168.1.10" or
apache_config.vm.network :hostonly, "192.168.1.11".

Enyoj playing around with it.

This approach show how easy it is with rpm and puppet to have a automated deployment. This scenario
here is simple and is a one machine with 2 vm approach but if you use a remote yum repo and put the
puppet manifest also remote with git or puppet master than you are ready for automated deployment on 
any number of machines you wont. Maybe you got the spirit for that.

If you have questions or have problem feel free to contact me.
pussinboots666@googlemail.com.


