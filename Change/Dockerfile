# -----------------------
# Stage 1: Build EJB jar
# -----------------------
FROM maven:3.9.6-eclipse-temurin-11 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests clean package

# -----------------------
# Stage 2: WildFly runtime
# -----------------------
FROM quay.io/wildfly/wildfly:latest

# Add a WildFly admin user (for management UI / health checks if needed)
# Username: admin / Password: Admin#70365
RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin#70365 --silent

# Deploy the EJB module
COPY --from=build /build/target/*.jar /opt/jboss/wildfly/standalone/deployments/Change.jar

# Expose HTTP and Management ports
EXPOSE 8080 9990

# Environment hints (not strictly required)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Start WildFly in standalone mode
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
