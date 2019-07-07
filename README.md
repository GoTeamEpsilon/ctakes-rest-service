# cTAKES Rest Service

The goal of this solution is to provide a JSON-based REST service to process unstructured clinical text through a smart natural language processing system in a fast, accurate, and easy to setup way. Note that the install instructions are designed for a local environment and no guidance is provided for a secured cloud/server deployment.

![img](./demo.png)

## Install
                                                                                                                                                                                                                                          
_(Based on Ubuntu 18.04)_

## Prerequisites

The following updates the list of available packages and installs necessary packages needed for this solution.

```
sudo apt-get update -y
sudo apt-get install -y maven subversion git unzip wget curl
```

## Java

This solution has been tested on Java JDK/JRE 8. Newer Java versions have been reported to have many bugs. The following installs the exact Java version and configures it so the default Java 11 is not used.

```
sudo apt-get install -y openjdk-8-jdk openjdk-8-jre-headless default-jdk

### SELECT Java 8 with most likely selection "2" and press enter.
sudo update-alternatives --config java
  Selection    Path                                            Priority   Status
------------------------------------------------------------
* 0            /usr/lib/jvm/java-11-openjdk-amd64/bin/java      1111      auto mode
  1            /usr/lib/jvm/java-11-openjdk-amd64/bin/java      1111      manual mode
  2            /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java   1081      manual mode

### SELECT Javac 8 with most likely selection "2" and press enter.
sudo  update-alternatives --config javac
  Selection    Path                                          Priority   Status
------------------------------------------------------------
* 0            /usr/lib/jvm/java-11-openjdk-amd64/bin/javac   1111      auto mode
  1            /usr/lib/jvm/java-11-openjdk-amd64/bin/javac   1111      manual mode
  2            /usr/lib/jvm/java-8-openjdk-amd64/bin/javac    1081      manual mode
```

## MySQL

The MySQL version to be installed must be 5.7, which is the default under Ubuntu 18.04. The following also adds a default user for cTAKES.

```
sudo apt-get install -y mysql-server mysql-client

### Select "low" password validity/deny "VALIDATE PASSWORD PLUGIN",
### enter "pass" as the password, and confirm all other dialogs.
sudo mysql_secure_installation
sudo service mysql restart
sudo mysql -u root -ppass
    mysql> ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'pass';
    mysql> FLUSH PRIVILEGES;
    mysql> EXIT;
```

## Tomcat

The following installs Apache Tomcat at version 8.5.42.

```
sudo useradd -m -U -d /opt/tomcat -s /bin/false tomcat
cd /tmp
### For whatever reason, when Apache Tomcat is updated, older versions are removed from the server.
### If the zip file is not found, you'll need to locate the latest version and update the following
### command, as well as a few others below. The file server is found here:
### http://www-us.apache.org/dist/tomcat/tomcat-8/
wget http://www-us.apache.org/dist/tomcat/tomcat-8/v8.5.42/bin/apache-tomcat-8.5.42.zip
unzip apache-tomcat-*.zip
sudo mkdir -p /opt/tomcat
sudo mv apache-tomcat-8.5.42 /opt/tomcat/
sudo ln -s /opt/tomcat/apache-tomcat-8.5.42 /opt/tomcat/latest
sudo chown -R tomcat: /opt/tomcat
sudo sh -c 'chmod +x /opt/tomcat/latest/bin/*.sh'
```

#### Tomcat Service Install

Using the Nano editor, create this file and note that it is opened for writing.

```
sudo nano /etc/systemd/system/tomcat.service
```

#### With Nano opened, paste the following with Ctrl + Shift + v. Save with with Ctrl + x, Y, enter.

```
[Unit]
Description=Tomcat 8.5 servlet container
After=network.target

[Service]
Type=forking

User=tomcat
Group=tomcat

Environment="JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64"
Environment="JAVA_OPTS=-Djava.security.egd=file:///dev/urandom"

Environment="CATALINA_BASE=/opt/tomcat/latest"
Environment="CATALINA_HOME=/opt/tomcat/latest"
Environment="CATALINA_PID=/opt/tomcat/latest/temp/tomcat.pid"
Environment="CATALINA_OPTS=-Xms4000m -Xmx4000m -server -XX:+UseParallelGC"

ExecStart=/opt/tomcat/latest/bin/startup.sh
ExecStop=/opt/tomcat/latest/bin/shutdown.sh

[Install]
WantedBy=multi-user.target
```

#### Start up Tomcat and initialize the service at boot-time.

```
sudo systemctl daemon-reload
sudo systemctl start tomcat
sudo systemctl enable tomcat
```

## Local Repository

The following pulls down and enters into the cTAKES Rest Service repository directory

```
cd ~/Desktop
git clone https://github.com/GoTeamEpsilon/ctakes-rest-service.git
cd ctakes-rest-service
```

## SQL Data Scripts

Load in all SQL data scripts in `./sno_rx_16ab_db`. This process may take several hours.

```
sudo mysql -u root -ppass < sno_rx_16ab_db/01_setup.sql
sudo mysql -u root -ppass < sno_rx_16ab_db/02_load.sql
sudo mysql -u root -ppass < sno_rx_16ab_db/03_load.sql
sudo mysql -u root -ppass < sno_rx_16ab_db/04_load.sql
sudo mysql -u root -ppass < sno_rx_16ab_db/05_load.sql
sudo mysql -u root -ppass < sno_rx_16ab_db/06_load.sql
sudo mysql -u root -ppass < sno_rx_16ab_db/07_load.sql
sudo mysql -u root -ppass < sno_rx_16ab_db/08_load.sql
```

## Building Codebase

The following instructs Maven (mvn) to install and build cTAKES proper as well as this codebase.

```
mkdir ctakes-codebase-area
cd ctakes-codebase-area
svn export 'https://svn.apache.org/repos/asf/ctakes/trunk'
cd trunk/ctakes-distribution
mvn install -Dmaven.test.skip=true
cd ../ctakes-assertion-zoner
mvn install -Dmaven.test.skip=true
cd ../../../ctakes-web-rest
mvn install
```

## Launch the Service

The following creates a Web Application Resource (WAR) file for Tomcat to serve.

```
sudo mv target/ctakes-web-rest.war /opt/tomcat/latest/webapps/
```

#### Real-time Tomcat Logs

While the deployed service is initializing, run the following in another terminal tab to see the logs.

```
sudo tail -f /opt/tomcat/latest/logs/catalina.out
```

## Access the Service

There are two ways to access the service:

1. Access the URL `http://localhost:8080/ctakes-web-rest/index.jsp` to use a webpage with a textbox. When the text is submitted, a JSON structure is returned in a popup with all relevant clinical mentions.

2. Any HTTP client such as cURL or Postman. Here is an example of a cURL request:

```
curl -X POST \
  'http://localhost:8080/ctakes-web-rest/service/analyze?pipeline=Default' \
  -H 'cache-control: no-cache' \
  -d 'Patient has hypertension'
```

## Technical Notes

- This solution assumes MySQL uses the default port 3306. 
- There is a minimum RAM requirement of 5 gigabytes. Tomcat is set to use up to 4 gigabytes.
- For an exact view of the custom dictionary used in this solution, please see the [following](https://github.com/GoTeamEpsilon/ctakes-rest-service/blob/master/ctakes-web-rest/src/main/resources/org/apache/ctakes/dictionary/lookup/fast/customDictionary.xml).


## License

Apache License, Version 2.0
