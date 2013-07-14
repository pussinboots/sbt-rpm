TIMESTAMP=$(date +%m%d%y%H%M%S)

mkdir -p /usr/share/tomcat/undeploy/${TIMESTAMP}/
mv /usr/share/tomcat/webapps/management.war /usr/share/tomcat/undeploy/${TIMESTAMP}/management.war
sleep 10
service tomcat stop