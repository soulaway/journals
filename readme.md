# Journals Web portal assembling guide

** Summary **

spring-boot secured web-app, with AutoConfiguration enabled

Data model: *User* may be a *Publisher* may have more than one *Subscribtion*. Publishers may subscribe and then publish/update/delete a pdf to some *Category*. Others may subscribe to *Category* for viewing any its *Journal*

contains: spring authorization, Thymeleaf templates, spring jpa/hibernate, spring boot-starter-mail SMTP

BOM version is updated to revision 1.4.1.RELEASE

an application properties are separate it as:

live properties *src/main/resources/application.properties* 

test properties *src/test/resources/application.properties*

** Pre-required **

Please install following environment by default

* JDK. from Oracle <http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html> or OpenJdk <http://openjdk.java.net/install/>
	
* Maven. From <https://maven.apache.org/download.cgi> manual <http://maven.apache.org/install.html>
	
* Docker. From <https://docs.docker.com/engine/installation/>

** Build environment versions:**

Java *java-8-openjdk-amd64 (Java version: 1.8.0_91, vendor: Oracle Corporation)*

Maven *Apache Maven 3.0.5*

Docker *version 1.12.2, build bb80604*

** Instructions to create and initialize the database**

* Download mysql docker image 

> docker pull mysql-server:5.6

* Run mysql single instance container

> docker run --name mysql1 -e MYSQL_ONETIME_PASSWORD=yes -e MYSQL_ROOT_PASSWORD=journals -e MYSQL_USER=journals -e MYSQL_PASSWORD=journals -e MYSQL_DATABASE=journals -d -p 3306:3306 mysql/mysql-server:5.6

** Building the application, running integration tests **

> mvn clean package -Ptest -Dupload-dir=/some/dir/to/store/files

where the *upload-dir* property is a location of the files repository

** Coverage reports**

 enabled for *test* maven profile by default 

 avaliable at *{project.dir}/target/site/jacoco/index.html*

** Run application**

*assuming that live database is up and running* 

> mvn spring-boot:run

live logs are avaliable at *{project.dir}/logs/journals.log*

test web portal page on <http://localhost:8080/>

** Notes **

The list of all avaliable ApplicationPropertis of Spring Boot are at:

<http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html>


for being able to deliver any messages using javax.mail throw Google gmail account, sending account should disable lesssecureapps should be allowed to access the account, ref:

<https://www.google.com/settings/security/lesssecureapps>

