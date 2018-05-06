FROM tomcat:8.0
COPY . .
ADD install.sh ./install.sh
RUN chmod +x ./install.sh
RUN ./install.sh
RUN openssl req -new -newkey rsa:4096 -sha256 -days 1200 -nodes -x509 -keyout server.key -out server.crt -subj '/CN=localhost'
