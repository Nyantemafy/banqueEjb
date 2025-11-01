FROM quay.io/wildfly/wildfly:latest

USER root

# Copier le fichier de devises
COPY app1-lecture-devises/devises.txt /opt/jboss/wildfly/standalone/deployments/

USER jboss

# Exposer le port HTTP 8081
EXPOSE 8081

# Démarrer WildFly en écoutant sur 8081
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-Djboss.http.port=8081"]

