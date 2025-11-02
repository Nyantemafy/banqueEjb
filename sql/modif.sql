-- Modifications légères à apporter à votre base de données existante

-- Ajout de colonnes supplémentaires à la table transaction
-- Ces colonnes permettent de gérer mieux les virements et les devises
ALTER TABLE transaction ADD COLUMN IF NOT EXISTS compte_beneficiaire VARCHAR(50);
ALTER TABLE transaction ADD COLUMN IF NOT EXISTS devise VARCHAR(10) DEFAULT 'AR';
ALTER TABLE transaction ADD COLUMN IF NOT EXISTS statut VARCHAR(20) DEFAULT 'EN_ATTENTE';
ALTER TABLE transaction ADD COLUMN IF NOT EXISTS reference VARCHAR(100);

-- Optionnel: Ajout d'index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_transaction_statut ON transaction(statut);
CREATE INDEX IF NOT EXISTS idx_transaction_date ON transaction(date_transaction);
CREATE INDEX IF NOT EXISTS idx_transaction_compte ON transaction(id_compteCourant);

-- Données de test supplémentaires (optionnel)

-- Créer un utilisateur admin de test
INSERT INTO utilisateur (username, password, id_role, id_direction, id_status) 
VALUES ('admin', 'admin123', 1, 1, 1)
ON CONFLICT (id_user) DO NOTHING;

-- Créer un utilisateur agent de test
INSERT INTO actionRole (nomTable, id_action, id_role) 
VALUES ('compteCourant', 1, 3)
ON CONFLICT (id_actionRole) DO NOTHING;

INSERT INTO utilisateur (id_user, username, password, id_actionRole, id_direction, id_status) 
VALUES (2, 'agent', 'agent123', 2, 2, 1)
ON CONFLICT (id_user) DO NOTHING;

-- Créer un utilisateur client de test
INSERT INTO actionRole (nomTable, id_action, id_role) 
VALUES ('compteCourant', 2, 2)
ON CONFLICT (id_actionRole) DO NOTHING;

INSERT INTO utilisateur (username, password, id_actionRole, id_direction, id_status) 
VALUES ('client', 'client123', 3, 4, 1)
ON CONFLICT (id_user) DO NOTHING;

-- Créer des comptes courants de test
INSERT INTO compteCourant (id_compteCourant, solde, date_ouverture, id_user, id_status) 
VALUES (1, 5000000.00, '2024-01-01', 3, 1)
ON CONFLICT (id_compteCourant) DO NOTHING;

INSERT INTO compteCourant (id_compteCourant, solde, date_ouverture, id_user, id_status) 
VALUES (2, 3000000.00, '2024-01-01', 2, 1)
ON CONFLICT (id_compteCourant) DO NOTHING;

-- Vérifier que la séquence est à jour
SELECT setval('role_id_role_seq', (SELECT MAX(id_role) FROM role), true);
SELECT setval('action_id_action_seq', (SELECT MAX(id_action) FROM action), true);
SELECT setval('direction_id_direction_seq', (SELECT MAX(id_direction) FROM direction), true);
SELECT setval('status_id_status_seq', (SELECT MAX(id_status) FROM status), true);
SELECT setval('type_id_type_seq', (SELECT MAX(id_type) FROM type), true);