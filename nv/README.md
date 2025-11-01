## Instructions de Déploiement

### 1. Compilation
```bash
# Compiler App1
cd app1-lecture-devises
mvn clean package

# Compiler App2
cd ../app2-multiplication
mvn clean package

# Compiler App3
cd ../app3-interface
mvn clean package
```

### 2. Déploiement Wildfly Local
Copier les WAR dans `$WILDFLY_HOME/standalone/deployments/`:
```bash
cp app2-multiplication/target/app2-multiplication.war $WILDFLY_HOME/standalone/deployments/
cp app3-interface/target/app3-interface.war $WILDFLY_HOME/standalone/deployments/
```

### 3. Lancer Docker
```bash
docker-compose up
```

### 4. Démarrer Wildfly Local
```bash
$WILDFLY_HOME/bin/standalone.sh -Djboss.socket.binding.port-offset=100
```

### 5. Accès aux Applications
- **App1 (Docker)**: http://localhost:8080/app1-devises/devises
- **App2 (Local)**: http://localhost:8180/app2-multiplication/multiplication
- **App3 (Local - Interface)**: http://localhost:8180/app3-interface/

---

## Configuration Supplémentaire

### jboss-ejb-client.properties (pour App2)
Créer `app2-multiplication/src/main/resources/jboss-ejb-client.properties`:
```properties
endpoint.name=client-endpoint
remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED=false
remote.connections=default

remote.connection.default.host=wildfly-docker
remote.connection.default.port=8080
remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS=false
remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT=false
remote.connection.default.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS=JBOSS-LOCAL-USER
```

### WEB-INF/jboss-deployment-structure.xml (pour App2 et App3)

**App2**: `app2-multiplication/src/main/webapp/WEB-INF/jboss-deployment-structure.xml`

## Workflow Complet de A à Z

```bash
# 1. Créer la structure du projet
mkdir -p ejb-devises-project/{app1-lecture-devises,app2-multiplication,app3-interface,docker}

# 2. Créer tous les fichiers Java, JSP, XML selon la structure ci-dessus

# 3. Compiler les applications
cd ejb-devises-project
mvn clean package -f app1-lecture-devises/pom.xml
mvn clean package -f app2-multiplication/pom.xml
mvn clean package -f app3-interface/pom.xml

# 4. Ajouter wildfly-docker au fichier hosts
echo "127.0.0.1 wildfly-docker" | sudo tee -a /etc/hosts

# 5. Lancer Docker Compose
docker-compose up -d

# 6. Attendre que Docker soit prêt (environ 30 secondes)
sleep 30

# 7. Copier les WAR dans Wildfly local
cp app2-multiplication/target/app2-multiplication.war $WILDFLY_HOME/standalone/deployments/
cp app3-interface/target/app3-interface.war $WILDFLY_HOME/standalone/deployments/

# 8. Démarrer Wildfly local avec offset
$WILDFLY_HOME/bin/standalone.sh -Djboss.socket.binding.port-offset=100

# 9. Tester les applications
# App1: http://localhost:8080/app1-devises/devises
# App2: http://localhost:8180/app2-multiplication/multiplication
# App3: http://localhost:8180/app3-interface/
```

---

## Notes Importantes

1. **Port Offset**: Le Wildfly local utilise un offset de 100, donc:
   - HTTP: 8180 (au lieu de 8080)
   - Management: 10090 (au lieu de 9990)

2. **Ordre de démarrage**:
   - D'abord Docker (App1)
   - Ensuite Wildfly local (App2 et App3)

3. **Communication EJB**:
   - App2 → App1: EJB Remote via HTTP
   - App3 → App2: EJB Local (même JVM)

4. **Fichier devises.txt**: Placé dans le conteneur Docker au moment du build

5. **Java 8 & Wildfly 26.1.3**: Versions fixes comme demandé

---

## Tester le Fonctionnement

### Test 1: App1 - Lecture fichier
```bash
curl http://localhost:8080/app1-devises/devises
```
Devrait afficher toutes les devises du fichier.

### Test 2: App2 - Multiplication
```bash
curl http://localhost:8180/app2-multiplication/multiplication
```
Devrait afficher les devises avec cours × 2.

### Test 3: App3 - Interface
Ouvrir dans un navigateur: `http://localhost:8180/app3-interface/`
- Sélectionner une devise dans la liste déroulante
- Cliquer sur "Afficher le cours multiplié"
- Voir le cours multiplié par 2



    
        
            
            
            
            
        
    
