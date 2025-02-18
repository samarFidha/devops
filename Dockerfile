FROM alpine

RUN apk add nginx

EXPOSE 80

CMD "nginx"