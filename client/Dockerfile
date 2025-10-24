FROM openjdk:17-jdk-slim
WORKDIR /app
COPY ./jboss-ejb-client.properties /app/
COPY ./src /app/src
RUN javac -d /app/classes /app/src/*.java
CMD ["java", "-cp", "classes:./jboss-ejb-client.properties", "Main"]
