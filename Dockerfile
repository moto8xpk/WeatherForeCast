# Use a lightweight Java runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /work

# Create a non-root user (id 1001) for better security
RUN useradd -r -u 1001 quarkus && chown -R quarkus /work
USER 1001

# Copy all the Quarkus fast-jar files into the container
COPY build/quarkus-app/lib/ /work/lib/
COPY build/quarkus-app/*.jar /work/
COPY build/quarkus-app/app/ /work/app/
COPY build/quarkus-app/quarkus/ /work/quarkus/

# Expose Quarkus port
EXPOSE 8080

# Start the app
ENTRYPOINT ["java","-jar","/work/quarkus-run.jar"]
