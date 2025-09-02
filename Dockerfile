FROM eclipse-temurin:21-jre-alpine
WORKDIR /work

# Create group/user with uid/gid 1001 (no home, no password)
RUN addgroup -S quarkus -g 1001 \
 && adduser -S -D -H -u 1001 -G quarkus quarkus \
 && chown -R quarkus:quarkus /work

USER quarkus

# Copy Quarkus fast-jar layout
COPY --chown=quarkus:quarkus build/quarkus-app/lib/ /work/lib/
COPY --chown=quarkus:quarkus build/quarkus-app/*.jar /work/
COPY --chown=quarkus:quarkus build/quarkus-app/app/ /work/app/
COPY --chown=quarkus:quarkus build/quarkus-app/quarkus/ /work/quarkus/

EXPOSE 8080
ENTRYPOINT ["java","-jar","/work/quarkus-run.jar"]
