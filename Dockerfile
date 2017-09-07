<<<<<<< HEAD
FROM ubuntu:latest
ADD install.sh /install.sh
RUN chmod +x install.sh
RUN /install.sh
CMD ["/usr/bin/supervisord", "-n"]
=======
FROM python:2.7.13-alpine3.6

WORKDIR /usr/src/app

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD [ "python", "./parser.py" ]
>>>>>>> 132e32c... dockerize
