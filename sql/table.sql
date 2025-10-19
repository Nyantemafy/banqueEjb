-- Création base
CREATE DATABASE banque_db;

-- Utilisateur (si tu veux un user dédié)
CREATE USER banque_user WITH ENCRYPTED PASSWORD 'banque_pass';

-- Donner accès
GRANT ALL PRIVILEGES ON DATABASE banque_db TO banque_user;

\c banque_db

-- Table comptes_courants
CREATE TABLE comptes_courants (
    numero_compte VARCHAR(255) PRIMARY KEY,
    proprietaire VARCHAR(255),
    solde NUMERIC(19,2),
    date_creation TIMESTAMP
);

-- Table transactions
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    numero_compte VARCHAR(255),
    montant NUMERIC(19,2),
    type VARCHAR(50),
    date_transaction TIMESTAMP,
    CONSTRAINT fk_transaction_compte
        FOREIGN KEY (numero_compte)
        REFERENCES comptes_courants (numero_compte)
        ON DELETE CASCADE
);

-- ======================
-- Table : demandes_pret
-- ======================
CREATE TABLE demandes_pret (
    numero_demande      VARCHAR(50) PRIMARY KEY,
    numero_client       VARCHAR(50) NOT NULL,
    montant_demande     NUMERIC(18,2) NOT NULL,
    duree_en_mois       INTEGER NOT NULL,
    objet_pret          VARCHAR(255),
    statut              VARCHAR(20) DEFAULT 'EN_ATTENTE', -- EN_ATTENTE, APPROUVEE, REJETEE
    date_demande        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_decision       TIMESTAMP NULL,
    taux_propose        DOUBLE PRECISION,
    mensualite_calculee NUMERIC(18,2),
    motif_rejet         VARCHAR(255)
);

-- ======================
-- Table : prets
-- ======================
CREATE TABLE prets (
    numero_pret      VARCHAR(50) PRIMARY KEY,
    numero_demande   VARCHAR(50) REFERENCES demandes_pret(numero_demande) ON DELETE CASCADE,
    numero_client    VARCHAR(50) NOT NULL,
    montant_initial  NUMERIC(18,2) NOT NULL,
    montant_restant  NUMERIC(18,2) NOT NULL,
    taux_interet     DOUBLE PRECISION,
    duree_en_mois    INTEGER,
    mensualite       NUMERIC(18,2),
    date_debut       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut_pret      VARCHAR(20) DEFAULT 'ACTIF', -- ACTIF, REMBOURSE, SUSPENDU
    echeances_payees INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS clients (
  numero_client    VARCHAR(50) PRIMARY KEY,
  nom              VARCHAR(100) NOT NULL,
  prenom           VARCHAR(100) NOT NULL,
  email            VARCHAR(200),
  telephone        VARCHAR(50),
  date_inscription TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_clients_nom ON clients (nom);
CREATE INDEX IF NOT EXISTS idx_clients_prenom ON clients (prenom);

-- Ajout colonnes pour rôles et mot de passe (si absentes)
ALTER TABLE clients ADD COLUMN IF NOT EXISTS role VARCHAR(20);
ALTER TABLE clients ADD COLUMN IF NOT EXISTS mot_de_passe VARCHAR(255);