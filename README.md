#AIRTEL WYNKSTUIDO MIDDLEWARE

## PRE-REQUISITES	
*  The system will require a recent version of the Java Development Kit [JDK 1.7 is recommended].
*  A recent version of Apache Tomcat Server [7 or 8] for deployment.

## QUICKSTART
To get this project running on your local environment, make sure you have installed Maven
in your system and do the following:
```
$ git clone git@bitbucket.org:accedo/wynk-middleware.git -b develop
$ cd wynk-middleware
$ mvn compile
```
More details about maven tasks is available below

## DEPENDENCIES
## Maven will download and configure all dependencies listed in the pom.xml file when 'mvn compile' is executed. There are no other external dependencies outside of this.


## FOLDERS
	wynk-middleware
	|-- src 									Source Code
	|   |-- main
	|	|   |-- java
	|	|   |   `-- com  
	|	|	|		`-- accedo
    |	|   |           `-- wynkstudio          Java Classes are included in the varoius folders inside
	|	|   |
	|	|   |-- resources                       Resources like property files & specs are here
	|	|   |-- webapp                          Web application sources
	|	|
	|	|-- test								Testing code
	|	|   |-- java
	|	|   |   `-- com  
	|	|	|		`-- accedo
    |	|   |           `-- wynkstudio			Unit test classes are here
	|	|   |
	|
	|-- target									Used to house all output of the build
	|
	|-- log 									Log files 
	|
	|-- libs									Contains our custom dependency JARs
	|
	

## FILES
*	pom.xml
	Maven's config file.
	All dependencies and configuration of the Project is included here.


##MAVEN
###Tasks

*	```mvn test```
	Compile and run your test sources.

*	```mvn clean package```
	This task will produce a build and package the app in the packaging format specified in the pom.xml
	which could be a jar, war etc. 

*	```mvn install```
	Installs the generated artifact in your local repository.

* 	```mvn clean````
	This will remove the target directory with all the build data before starting so that it is fresh.

## DEPLOYMENT
# 	For now, we have to manually put the generated package[war] in Tomcat for deployment. Once the 
# 	build & deployment automation is done Readme will be updated accordingly.

## VARNISH API CACHE
*	Run the following command to install Varnish is a UNIX based system [Ubuntu/Debian]
	``` sudo apt-get install varnish```

* To start, stop and restart Varnish
	``` service varnish start```
	``` service varnish stop```
	``` service varnish restart```

* Set the following in /etc/default/varnish file to configure the port varnish will run on
	DAEMON_OPTS="-a :80 \
             -T localhost:6082 \
             -f /etc/varnish/default.vcl \
             -S /etc/varnish/secret \
             -s malloc,256m"

* Set the following in /etc/varnish/default.vcl file to configure the route of the our app to be cached
	backend default {
      .host = "127.0.0.1";
      .port = "8080";
	}
 