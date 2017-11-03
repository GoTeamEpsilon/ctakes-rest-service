FROM ubuntu:latest
ADD install.sh /install.sh
RUN chmod +x install.sh
RUN /install.sh
CMD ["/usr/bin/supervisord", "-n"]
