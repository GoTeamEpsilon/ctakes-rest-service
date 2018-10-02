(Note: this software is in a beta state)

# cTAKES Rest Service

The goal of this solution is to provide a JSON-based REST service to process unstructured clinical text through a smart natural language processing system in a fast, accurate, and easy to setup way.

## Install

1. Install Java 8, MySQL 5.7, Tomcat 8, Maven 3.5, Subversion, and Git.
2. Setup MySQL on port 3066 with the username/password set to root/pass (you can use custom configurations and specify them in `./ctakes-web-rest/src/main/resources/org/apache/ctakes/dictionary/lookup/fast/customDictionary.xml`).
3. Git clone this repository: `git clone https://github.com/GoTeamEpsilon/ctakes-rest-service.git`.
4. Load in all SQL scripts in `./sno_rx_16ab_db`. This process may take several hours.
5. `cd` into the repository and run the following to pull down cTAKES:

```
mkdir ctakes-codebase-area
cd ctakes-codebase-area
svn export 'https://svn.apache.org/repos/asf/ctakes/trunk'
```

6. Build the appropriate cTAKES modules with the following:

```
cd trunk/ctakes-distribution
mvn install -Dmaven.test.skip=true
cd ../ctakes-assertion-zoner
mvn install -Dmaven.test.skip=true
```

7. Navigate back to the main codebase and build:
```
cd ../../../ctakes-web-rest
mvn install
```

8. Deploy the War file:

```
sudo mv target/ctakes-web-rest.war /var/lib/tomcat8/webapps

# useful for debugging (uncomment):
# tail -f /var/log/tomcat8/catalina.out
```

9. Access the URL `http://localhost:8080/ctakes-web-rest/index.jsp` for testing the REST service.

10. To test using a REST client like Postman, use the following URL: ` http://localhost:8080/ctakes-web-rest/service/analyze?pipeline=Default` (ensure to use POST method and RAW response)


## License

Apache License, Version 2.0
