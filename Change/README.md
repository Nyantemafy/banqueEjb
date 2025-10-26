# Change EJB (Devises) - Docker

Ce module EJB fournit la lecture des cours de change depuis `rates.txt` et des APIs de conversion via EJB.

## Contenu
- EJB: `com.banque.change.ejb.ChangeBean` (remote: `com.banque.change.remote.ChangeRemote`)
- Ressource: `src/main/resources/rates.txt`
- Dockerfile: exécute le module dans WildFly

## Construction et exécution (Docker)

1) Construire l'image
```
docker build -t change-ejb:latest .
```

2) Lancer le conteneur
```
docker run --rm -p 8080:8080 -p 9990:9990 --name change-ejb change-ejb:latest
```
- WildFly démarre et déploie `Change.jar`
- Un utilisateur admin WildFly est créé (admin / Admin#70365) pour la console de gestion (port 9990)

3) Vérifier le déploiement
- Accéder aux logs du conteneur et vérifier la ligne: `Loaded currencies: ...`
- Dans la console de gestion (http://localhost:9990), vérifier le déploiement.

## JNDI attendu (WildFly)
- Global JNDI (le plus courant):
```
java:global/Change/ChangeBean!com.banque.change.remote.ChangeRemote
```
- Selon la configuration/nom final du module, vous pouvez voir un nom comme:
```
java:global/Change-1.0-SNAPSHOT/ChangeBean!com.banque.change.remote.ChangeRemote
```
- Le serveur affiche le nom exact au démarrage dans les logs. Utilisez ce nom si différent.

## Intégration avec BanqueCentral

- Si `BanqueCentral` est déployé dans le **même serveur WildFly** que `Change`, le lookup in-container fonctionnera avec `EJBLocator.lookupChangeBean()`.
- Si `BanqueCentral` tourne **en dehors** de ce serveur (lookup EJB distant), il faudra configurer un `InitialContext` distant (http-remoting) et dépendances client WildFly. Dans ce cas, adaptez `EJBLocator` pour fournir un contexte distant et utilisez le JNDI global affiché par WildFly.

## Fichier des taux
- Format: `nom_devise;date_debut;date_fin;cours` (cours = MGA par 1 unité de devise)
- Exemple fourni:
```
MGA; ; ;1
EUR;2025-01-01;2025-12-31;4800
USD;2025-01-01;2025-12-31;4400
KMF;2025-01-01;2025-12-31;10
ZAR;2025-01-01;2025-12-31;240
```
- Le bean tente de charger `"/rates.txt"` puis `"rates.txt"` sur le classpath, et loggue une alerte si introuvable.

## Notes
- Ports exposés: 8080 (HTTP), 9990 (management)
- Mémoire JVM par défaut: `-Xms256m -Xmx512m` (modifiable via `JAVA_OPTS`)
