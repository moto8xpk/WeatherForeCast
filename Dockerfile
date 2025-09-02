FROM gcr.io/distroless/java21-debian12
WORKDIR /work
# Run as non-root
USER 1001

# Copy Quarkus fast-jar layout
COPY --chown=1001:1001 build/quarkus-app/lib/ /work/lib/
COPY --chown=1001:1001 build/quarkus-app/*.jar /work/
COPY --chown=1001:1001 build/quarkus-app/app/ /work/app/
COPY --chown=1001:1001 build/quarkus-app/quarkus/ /work/quarkus/

EXPOSE 8080
ENTRYPOINT ["java","-jar","/work/quarkus-run.jar"]
