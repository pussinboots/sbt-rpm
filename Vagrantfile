# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant::Config.run do |config|

   config.vm.define :web do |web_config|
	  web_config.vm.box_url = "https://dl.dropboxusercontent.com/u/35824962/vagrant/CentOS64_201307"
	  web_config.vm.box = "CentOS64_201307"
	  web_config.vm.host_name = "helloworld.com"
	  web_config.vm.network :hostonly, "192.168.1.10"
   end
   
   config.vm.define :apache do |apache_config|
   	  apache_config.vm.box_url = "https://dl.dropboxusercontent.com/u/35824962/vagrant/CentOS64_201307"
	  apache_config.vm.box = "CentOS64_201307"
	  apache_config.vm.host_name = "apache.com"
	  apache_config.vm.network :hostonly, "192.168.1.11"
   end
   
   config.vm.share_folder "local-yumrepo", "/yum-repo", "yum-repo"
   # Enable the Puppet provisioner
   config.vm.provision :puppet do |puppet|
	puppet.options = "--verbose --debug"
	puppet.manifest_file  = "site.pp"
   end
end

