\c banque_db

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

CREATE TABLE compteDepot(
   id_compteDepot INTEGER,
   montant_initial NUMERIC(15,2)  ,
   taux_interet NUMERIC(15,2)  ,
   date_debut DATE,
   date_fin DATE,
   id_user INTEGER,
   id_status INTEGER,
   PRIMARY KEY(id_compteDepot),
   FOREIGN KEY(id_user) REFERENCES utilisateur(id_user),
   FOREIGN KEY(id_status) REFERENCES status(id_status)
);

CREATE TABLE depot_operation(
   id_op INTEGER,
   montant NUMERIC(15,2)  ,
   date_op DATE,
   id_compteDepot INTEGER,
   id_type INTEGER,
   PRIMARY KEY(id_op),
   FOREIGN KEY(id_compteDepot) REFERENCES compteDepot(id_compteDepot),
   FOREIGN KEY(id_type) REFERENCES type(id_type)
);

CREATE TABLE credit(
   id_credit INTEGER,
   montant_initial NUMERIC(15,2)  ,
   taux NUMERIC(15,2)  ,
   duree_mois INTEGER,
   mensualite NUMERIC(18,2)  ,
   solde_rest NUMERIC(18,2)  ,
   date_debut DATE,
   id_status INTEGER,
   id_user INTEGER,
   PRIMARY KEY(id_credit),
   FOREIGN KEY(id_status) REFERENCES status(id_status),
   FOREIGN KEY(id_user) REFERENCES utilisateur(id_user)
);

CREATE TABLE credit_echeance(
   id_echeance INTEGER,
   numero_echeance INTEGER,
   date_echeance DATE,
   montant_total NUMERIC(15,2)  ,
   capital NUMERIC(15,2)  ,
   interet NUMERIC(15,2)  ,
   solde_rest VARCHAR(50) ,
   id_status INTEGER,
   id_credit INTEGER,
   PRIMARY KEY(id_echeance),
   FOREIGN KEY(id_status) REFERENCES status(id_status),
   FOREIGN KEY(id_credit) REFERENCES credit(id_credit)
);

ALTER TABLE role
    ALTER COLUMN id_role ADD GENERATED ALWAYS AS IDENTITY;

ALTER TABLE action
    ALTER COLUMN id_action ADD GENERATED ALWAYS AS IDENTITY;

ALTER TABLE direction
    ALTER COLUMN id_direction ADD GENERATED ALWAYS AS IDENTITY;

ALTER TABLE actionRole
    ALTER COLUMN id_actionRole ADD GENERATED ALWAYS AS IDENTITY;

ALTER TABLE status
    ALTER COLUMN id_status ADD GENERATED ALWAYS AS IDENTITY;

ALTER TABLE type
    ALTER COLUMN id_type ADD GENERATED ALWAYS AS IDENTITY;

ALTER TABLE utilisateur
    ALTER COLUMN id_user ADD GENERATED ALWAYS AS IDENTITY;

ALTER TABLE compteCourant
    ALTER COLUMN id_compteCourant ADD GENERATED ALWAYS AS IDENTITY;

ALTER TABLE transaction
    ALTER COLUMN id_transaction ADD GENERATED ALWAYS AS IDENTITY;

ALTER TABLE compteDepot
    ALTER COLUMN id_compteDepot ADD GENERATED ALWAYS AS IDENTITY;

ALTER TABLE depot_operation
    ALTER COLUMN id_op ADD GENERATED ALWAYS AS IDENTITY;

ALTER TABLE credit
    ALTER COLUMN id_credit ADD GENERATED ALWAYS AS IDENTITY;

ALTER TABLE credit_echeance
    ALTER COLUMN id_echeance ADD GENERATED ALWAYS AS IDENTITY;

-- Utilisateur
SELECT setval(pg_get_serial_sequence('utilisateur','id_user'),
              COALESCE((SELECT MAX(id_user) FROM utilisateur),0)+1,
              false);

-- Compte Courant
SELECT setval(pg_get_serial_sequence('comptecourant','id_comptecourant'),
              COALESCE((SELECT MAX(id_comptecourant) FROM "comptecourant"),0)+1,
              false);

-- Transaction
SELECT setval(pg_get_serial_sequence('transaction','id_transaction'),
              COALESCE((SELECT MAX(id_transaction) FROM transaction),0)+1,
              false);