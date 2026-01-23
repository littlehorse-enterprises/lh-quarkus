FROM amazoncorretto:21

RUN yum install findutils -y && \
    yum clean all && \
    rm -rf /var/cache/yum

# entrypoint
COPY docker-entrypoint.sh /gateway/

# jar app
COPY build/quarkus-app/lib/ /gateway/lib/
COPY build/quarkus-app/*.jar /gateway/
COPY build/quarkus-app/app/ /gateway/app/
COPY build/quarkus-app/quarkus/ /gateway/quarkus/

# native app
COPY --chmod=0755 build/quarkus-run /gateway/

ENTRYPOINT ["/gateway/docker-entrypoint.sh"]
CMD ["native"]
