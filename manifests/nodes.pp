node 'helloworld.com' {
    include baserepo
    include helloworld
}

node 'apache.com' {
    include apache
}

class baserepo {
   yumrepo { "Local-Repo":
      baseurl => "file:///yum-repo",
      descr => "Local yum repo",
      enabled => 1,
      gpgcheck => 0
   }
}