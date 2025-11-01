FROM quay.io/wildfly/wildfly:26.1.2.Final

USER root

# Copier le fichier de devises
COPY app1-lecture-devises/devises.txt /opt/jboss/wildfly/standalone/deployments/

# Copier le WAR
COPY app1-lecture-devises/target/app1-devises.war /opt/jboss/wildfly/standalone/deployments/

USER jboss

# Créer un utilisateur pour EJB remoting
RUN /opt/jboss/wildfly/bin/add-user.sh -a -u ejbuser -p ejbpass -g users

# Exposer les ports
EXPOSE 8081 8080 9990 4447

# Démarrer WildFly avec standalone-full.xml sur le port 8081
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-c", "standalone-full.xml", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "-Djboss.http.port=8081"]
