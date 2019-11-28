FROM alpine:latest  
WORKDIR /root/
RUN apk add --no-cache gcc musl-dev make
ADD dumb-init /root/dumb-init
ADD server-side-selection   /root/server-side-selection
ADD server-side-migration   /root/server-side-migration
ADD server-side-hudson      /root/server-side-hudson
COPY Makefile /root/Makefile
RUN CC="gcc -static" make

FROM busybox:latest
COPY --from=0 /root/bin/dumb-init /bin/
COPY --from=0 /root/bin/selection /www/cgi-bin/
COPY --from=0 /root/bin/migration /www/cgi-bin/
COPY --from=0 /root/bin/hudson /www/cgi-bin/
COPY html/* /www/
COPY simulate /www/cgi-bin/
EXPOSE 80
ENTRYPOINT [ "/bin/dumb-init", "--" ]
CMD [ "httpd", "-h", "/www", "-f" ]
