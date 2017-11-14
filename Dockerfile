FROM tomcat:8.0
COPY . .
ADD install.sh ./install.sh
RUN chmod +x ./install.sh
RUN ./install.sh
