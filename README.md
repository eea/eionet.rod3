Reporting Obligation Database
=============================

The application uses Liquibase to create and upgrade the database, and Thymeleaf as the templating engine.
You can find the layout template at src/main/webapp/WEB-INF/thymeleaf/layout.html.

Dependencies
------------
* Tomcat 9
* Java 1.8
* Spring 5
* Thymeleaf 3
* MySQL or H2 Database Engine

Automated tests
---------------
There are test examples of both controllers and data access objects using the Spring test package.

Building the .war file
----------------------
The default profile is using the docker maven plugin to setup an h2 database for use with the integration tests phase. To create a .war file for deployment with tomcat, you can run any of the following commands:

To run unit and integration tests before building, run:
```
$ mvn clean install
```
To skip the integration tests (not recommended), you can add -Dmaven.test.skip=true e.g
```
$ mvn clean install -Dmaven.test.skip=true
```

Deployment of the .war file
----------------------
The default configuration is to allow you to deploy to your own workstation directly. You install the target/rod.war to Tomcat's webapps directory as ROOT.war. You can make it create an initial user with administrator rights by setting system properties to configure the application.

On a CentOS system you can start Tomcat with the environment variable CATALINA_OPTS set to some value or add lines to /etc/sysconfig/tomcat that looks like this:
```
CATALINA_OPTS="-Dcas.service=http://localhost:8080 -Dinitial.username=myname"
CATALINA_OPTS="$CATALINA_OPTS -Ddb.url=jdbc:h2:tcp://localhost:8043//work/rod3 -Ddeploy.contexts=uat -Ddeploy.dropfirst=true"
```
These are the properties you can set:
```
db.driver        # org.mariadb.jdbc.Driver
db.url           # jdbc:mariadb://dbservice/rod3
db.username      # rod3
db.password      # secret
upload.dir
deploy.contexts  # prod
deploy.dropfirst # false
initial.username # myuser
initial.password # Not needed when integrated with CAS.
cas.service
cas.server.host
```
The default values are in src/main/resources/application.properties and src/main/resources/cas.properties.

Building a Docker image
-----------------------

After having built the .war file with maven, it can be directly used in docker containers thanks to the environmental configuration. The Dockerfile can be used to build a ready-to-deploy image of rod:
```
docker build -t eeacms/rod:latest .
```
To push the Docker image of Eionet ROD to Docker Hub:
```
docker push eeacms/rod:latest
```
