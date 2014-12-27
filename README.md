[![Build Status](https://travis-ci.org/pussinboots/sbt-rpm.svg?branch=master)](https://travis-ci.org/pussinboots/sbt-rpm)

This is a simple helloworld web archive (war) project.

That illustrates the use of sbt native packager (https://github.com/sbt/sbt-native-packager) 
plugin to build a simple automated deployment with RPM and puppet.

The basic things like register yum repo and install the helloworld rpm
from that repo is done by puppet. The dependencies of the helloworld rpm
like java and tomcat are managed by the rpm specification in the project/packaging.scala
file with the sbt native package plugin. 

This is a minimal puppet approach the rpm dependencies could also be managed
by puppet and also configured by puppet.

Requirements for development:
 - git (`apt-get install git`)
 - createrepo (is needed to create a local yum repo -> install with `apt-get install createrepo`)
 - rpmbuild (is needed to build a rom with maven and deploy it to the local yum repo -> install with `apt-get install rpm`)

Requirements for deployment:
 - vagrant (optional to test the puppet deployment -> install with `apt-get install vagrant`)
 - virtualbox (is needed for vagrant as vm provider -> `apt-get install virtualbox`) 

Used:
 - puppet (is only needed in the vagrant box and is prepared in the configured base box)
 - CentOS64_201307 (self created base box hosted by [dropbox](https://dl.dropboxusercontent.com/u/35824962/vagrant/CentOS64_201307)

You can install the requirements from above to your system or use the vgit tool to get a ready to use development virtual machine read below.

## vgit Approach

Requirements to use vgit:
 - vagrant (optional to test the puppet deployment -> install with `apt-get install vagrant`)
 - virtualbox (is needed for vagrant as vm provider -> `apt-get install virtualbox`) 

Use the [vgit](https://github.com/pussinboots/vagrant-git) npm tool to start a virtual machine which contains all project dependencies ready for development. Follow the instruction to install vgit tool and start the development environment:
`vgit repo pussinboots/sbt-rpm`
It could take some minutes to download the development environment image. The result is a started ubuntu 12.10 as virtualbox. This virtual box can be used to perform the following steps. 

## Development

To build the project it doesn't matter if you use the offered virtualbox image or your own environment only the needed requirements are meet.

1) run sbt console with
   `sbt` <br />
2) run command to build war
   `package-war` <br />
3) run command to build rpm
   `rpm:package-bin` <br />
4) then copy the build rpm from target/rpm/RPMS/noarch/ to the yum-repo/ folder <br />
5) run command createrepo
   `createrepo yum-repo` <br />
6) then a new rpm version was build and can be deployed with puppet <br />

##Deployment

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

Enjoy playing around with it.

This approach show how easy it is with rpm and puppet to have a automated deployment. This scenario
here is simple and is a one machine with 2 vm approach but if you use a remote yum repo and put the
puppet manifest also remote with git or puppet master than you are ready for automated deployment on 
any number of machines you wont. Maybe you got the spirit for that.

If you have questions or have problem feel free to contact me.
pussinboots666@googlemail.com.


