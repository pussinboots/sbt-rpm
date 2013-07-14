# Basic Puppet Apache manifest
define yum_update() {
	exec { 'yum update':
		command => '/usr/bin/yum -y update'
	}
}