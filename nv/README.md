
mvn clean package -f app1-lecture-devises/pom.xml
mvn clean package -f app2-multiplication/pom.xml
mvn clean package -f app3-interface/pom.xml

docker-compose up -d

standalone.bat -Djboss.socket.binding.port-offset=100

Ouvrir la CLI:
docker exec -it wildfly-docker /opt/jboss/wildfly/bin/jboss-cli.sh -c
Puis coller l’une des séquences ci-dessus.


Activer REST:
standalone.bat -Djboss.socket.binding.port-offset=100 -Dclient.mode=rest
/system-property=client.mode:add(value=rest)
:reload


Revenir à EJB:
/system-property=client.mode:write-attribute(name=value, value=ejb)
:reload
Supprimer la propriété (revient au défaut EJB):
/system-property=client.mode:remove
:reload