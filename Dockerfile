FROM ubuntu:latest
ADD install.sh /install.sh
RUN chmod +x install.sh
RUN /install.sh
# This is a big hack (temporary)
CMD ["sleep", "99999"]
