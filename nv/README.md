\c banque_db
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


implement moi les classes pour ces tables : 
CREATE TABLE role(
   id_role INTEGER,
   libelle VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_role)
);

CREATE TABLE action(
   id_action INTEGER,
   libelle VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_action)
);

CREATE TABLE direction(
   id_direction INTEGER,
   niveau INTEGER NOT NULL,
   libelle VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_direction)
);

CREATE TABLE actionRole(
   id_actionRole INTEGER,
   nomTable VARCHAR(50)  NOT NULL,
   id_action INTEGER,
   id_role INTEGER,
   PRIMARY KEY(id_actionRole),
   FOREIGN KEY(id_action) REFERENCES action(id_action),
   FOREIGN KEY(id_role) REFERENCES role(id_role)
);

CREATE TABLE status(
   id_status INTEGER,
   libelle VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_status)
);

CREATE TABLE type(
   id_type INTEGER,
   libelle VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_type)
);

CREATE TABLE utilisateur(
   id_user INTEGER,
   username VARCHAR(50)  NOT NULL,
   password VARCHAR(50)  NOT NULL,
   id_actionRole INTEGER,
   id_direction INTEGER,
   id_status INTEGER,
   PRIMARY KEY(id_user),
   FOREIGN KEY(id_actionRole) REFERENCES actionRole(id_actionRole),
   FOREIGN KEY(id_direction) REFERENCES direction(id_direction),
   FOREIGN KEY(id_status) REFERENCES status(id_status)
);

CREATE TABLE compteCourant(
   id_compteCourant INTEGER,
   solde NUMERIC(15,2)  ,
   date_ouverture DATE NOT NULL,
   id_user INTEGER,
   id_status INTEGER,
   PRIMARY KEY(id_compteCourant),
   FOREIGN KEY(id_user) REFERENCES utilisateur(id_user),
   FOREIGN KEY(id_status) REFERENCES status(id_status)
);

CREATE TABLE transaction(
   id_transaction INTEGER,
   montant NUMERIC(15,2)   NOT NULL,
   date_transaction DATE,
   id_compteCourant INTEGER,
   id_type INTEGER,
   PRIMARY KEY(id_transaction),
   FOREIGN KEY(id_compteCourant) REFERENCES compteCourant(id_compteCourant),
   FOREIGN KEY(id_type) REFERENCES type(id_type)
);

j'ai deja ces donne la : INSERT INTO status (id_status, libelle) VALUES (1, 'Actif');
INSERT INTO status (id_status, libelle) VALUES (2, 'Inactif');
INSERT INTO status (id_status, libelle) VALUES (3, 'En attente');

-- Insérer des types
INSERT INTO type (id_type, libelle) VALUES (1, 'DEPOT');
INSERT INTO type (id_type, libelle) VALUES (2, 'RETRAIT');
INSERT INTO type (id_type, libelle) VALUES (3, 'VIREMENT');

-- Insérer des rôles
INSERT INTO role (id_role, libelle) VALUES (1, 'ADMIN');
INSERT INTO role (id_role, libelle) VALUES (2, 'CLIENT');
INSERT INTO role (id_role, libelle) VALUES (3, 'AGENT');

-- Insérer des actions
INSERT INTO action (id_action, libelle) VALUES (1, 'CREATE');
INSERT INTO action (id_action, libelle) VALUES (2, 'READ');
INSERT INTO action (id_action, libelle) VALUES (3, 'UPDATE');
INSERT INTO action (id_action, libelle) VALUES (4, 'DELETE');

-- Insérer des directions
INSERT INTO direction (id_direction, niveau, libelle) VALUES (1, 1, 'Direction Générale');
INSERT INTO direction (id_direction, niveau, libelle) VALUES (2, 2, 'Direction Commerciale');
INSERT INTO direction (id_direction, niveau, libelle) VALUES (3, 3, 'Direction IT');
INSERT INTO direction (id_direction, niveau, libelle) VALUES (4, 0, 'simple user');

-- Insérer des actionRole
INSERT INTO actionRole (id_actionRole, nomTable, id_action, id_role) 
VALUES (1, 'compteCourant', 1, 1);

je veux une projet purement oriente objet, dans la classe utilisateur met une fonction pour s'authentifier et mettre dans une statefull les information sur sont role, actionRole[], Direction[] pour ces information on aurra plus besoin de prendre dans le base mais prendre dans le stateful directement; dans la compteCourant met l'acion vire(String compte, String compteBeneficiaire, String montant, String devise, String Date) pour faire une virement et retourne une objet virement, tout est on string car cela vient de l'interface tout les traitement et cast se ferra dans le backend, fait une fonction getSolde aussi dans la classe, fait aussi unefonctionVirementComplet qui serra une extend de virement mais au lieux de me rendre les information de l'envoyeur juste il me rendra aussi les information sur la personne qui recois; fait moi une classe virement avec ces controlle unitaire, Montant pas negatif, compte qui envoye non vide, date inferieur ou egal a la date d'aujourd'hui; car une virement ne se cree pas tout de suite on doit encore le faire valide par ces controls unitaire, mais aussi une controlle complex le gestion de plafond, une compte ne peut pas faire une virement pas plus de 10.000.000ar par jour; dans la classe virement il y aurra une fonction est Valide aussi alors; a note que temps qu'il est mieux d'avoir une objet le retout de ses fonction serrait toujours des objets mais c'est apres qu'on appel son classe DAO; fait une annule virement avant, d'ou on va juste mettre le status annuler, une annulement viremment apres d'ou on va faire une virement inverse et mettre la status annuler; dans change aussi on continuera d'ecrire dans le txt, on aurra une class change d'ou il y a une change qui retourne une objet change, correction avant, d'ou on va juste pouvoir changer de devise et une correction apres d'ou on va annuler l'ancien virement et faire une nouveau virement; du coup on aura dedans findOperationlier qui sera le tableau transaction; pour l'interface fait moi la login, et la personne non admin ne verra que sont solde et ces historiques de transaction avec le possibilite de convertir en la devise qu'il souhaite sur une liste deroulant; l'agent aura une champ pour faire entrer une virement et une nouveau devise; l'admin porait valider et annuler les transactions