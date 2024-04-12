FROM gradle:7.4.2-jdk17-alpine
WORKDIR /app
COPY . .

USER root
RUN chown -R gradle /app
USER gradle

EXPOSE
RUN ./gradlew bootRun --stacktrace