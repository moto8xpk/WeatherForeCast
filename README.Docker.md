# Step to build docker image for Forecast Application

## Building and running your application
1. Build package for this application: `./gradlew build`
2. Build image for this application: `docker build -t my-quarkus:jvm .`
3. (Optional) testing application: `docker run -p 8080:8080 -e OPENWEATHER_API_KEY='<openapi-key>' my-quarkus:jvm`

Your application will be available at http://localhost:8080.
