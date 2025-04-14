FROM tomcat:9.0.102-jre8
RUN rm -rf /usr/local/tomcat/conf/logging.properties /usr/local/tomcat/webapps/*
COPY target/rod.war /usr/local/tomcat/webapps/ROOT.war
