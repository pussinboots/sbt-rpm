# Basic Puppet Apache manifest

import "nodes"
import "clean"

class apache {
  exec { 'allow http connections':
    command => '/sbin/iptables -I INPUT 5 -m state --state NEW -p tcp --dport 80 -j ACCEPT'
  }
  
  exec { 'yum update':
    command => '/usr/bin/yum -y update'
  }

  package { "httpd":
    ensure => present,
    require => Exec['yum update', 'allow http connections'],
  }

  service { "httpd":
    ensure => running,
    require => Package["httpd"],
  }

  file { '/var/www/html/index.html':
    source => "/vagrant/index.html",
    notify => Service['httpd'],
    force  => true
  }
}

class helloworld {
  exec { 'yum update':
    command => '/usr/bin/yum clean expire-cache',
    require => Yumrepo["Local-Repo"],
  }
  
  exec { 'allow http connections on port 8080':
    command => '/sbin/iptables -I INPUT 5 -m state --state NEW -p tcp --dport 8080 -j ACCEPT'
  }

  file { '/usr/java':
    ensure => directory
  }
  
  file { '/usr/java/latest/':
    ensure => link,
    target => "/usr/lib/jvm/java-1.7.0/",
    force  => true,
    require => Exec['yum update', 'allow http connections on port 8080']
  }

  ## prepare handling of java and tomcat dependency with puppet instead of the rpm itself
  
  #package { ["java-1.7.0-openjdk-devel"]:
  #  ensure => present,
  #  require => File['/usr/java/latest/']
  #}
  
  #package { ["apache-tomcat-7.0.41-1.noarch"]:
  #  ensure => present,
  #  require => Package["java-1.7.0-openjdk-devel"],
  #} 
  
  package { ["helloworld"]:
    ensure => latest,
    require => File['/usr/java/latest/'],
    #require => Package["apache-tomcat-7.0.41-1.noarch"]
  }
}