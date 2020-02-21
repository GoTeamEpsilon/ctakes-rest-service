FROM maven:3-jdk-8 as build-ctakes

WORKDIR /tmp
COPY ./ctakes-web-rest /ctakes-web-rest
RUN wget -qO- http://muug.ca/mirror/apache-dist//ctakes/ctakes-4.0.0/apache-ctakes-4.0.0-src.tar.gz | tar xvz 
RUN cd apache-ctakes-4.0.0-src/ctakes-distribution && mvn install -Dmaven.test.skip=true
RUN cd apache-ctakes-4.0.0-src/ctakes-assertion-zoner && mvn install -Dmaven.test.skip=true
RUN cd /ctakes-web-rest && mvn install -Dmaven.test.skip=true


FROM tomcat:9-jdk8

COPY --from=build-ctakes /ctakes-web-rest/target/*.war /usr/local/tomcat/webapps/

EXPOSE 8080

CMD ["catalina.sh", "run"]
