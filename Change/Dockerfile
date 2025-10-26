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

# Add a WildFly admin user (Management console)
# Username: admin / Password: Admin#70365
RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin#70365 --silent

# Add an application user for remote EJB access
# Username: ejbuser / Password: ejbpass
RUN /opt/jboss/wildfly/bin/add-user.sh -a -u ejbuser -p ejbpass --silent

# Deploy the EJB module
COPY --from=build /build/target/*.jar /opt/jboss/wildfly/standalone/deployments/Change.jar

# Expose HTTP (application) and Management ports
EXPOSE 8080 9990

# Environment hints (heap size)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Start WildFly in standalone mode
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
