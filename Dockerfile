FROM tomcat:8.0
ADD install.sh ./install.sh
RUN chmod +x ./install.sh
RUN ./install.sh
