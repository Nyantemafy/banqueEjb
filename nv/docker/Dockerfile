FROM quay.io/wildfly/wildfly:latest

USER root

# Copier le fichier de devises
COPY app1-lecture-devises/devises.txt /opt/jboss/wildfly/standalone/deployments/

COPY app1-lecture-devises/target/app1-devises.war /opt/jboss/wildfly/standalone/deployments/app1-lecture-devises.war

USER jboss
# Create an application user used by remote EJB clients
RUN /opt/jboss/wildfly/bin/add-user.sh -a -u ejbuser -p ejbpass -g users

# Exposer le port HTTP 8081
EXPOSE 8081

# Démarrer WildFly en écoutant sur 8081
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-c", "standalone-full.xml", "-b", "0.0.0.0", "-Djboss.http.port=8081"]
