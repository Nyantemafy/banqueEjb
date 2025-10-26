INSERT INTO compteDepot (montant_initial, taux_interet, date_debut, date_fin, id_user, id_status) VALUES
(1000000, 5.5, '2025-01-01', '2025-12-31', 1, 1),
(5000000, 6.2, '2025-02-01', '2026-02-01', 2, 1);

INSERT INTO compteCourant (solde, date_ouverture, id_user, id_status)
VALUES (0.00, CURRENT_DATE, 1, 1);

INSERT INTO depot_operation (montant, date_op, id_compteDepot, id_type) VALUES
(1000000, '2025-01-01', 1, 1),   
(50000, '2025-03-01', 1, 2),     
(5000000, '2025-02-01', 2, 1);   

-- Insérer des status
INSERT INTO status (id_status, libelle) VALUES (1, 'Actif');
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
INSERT INTO actionRole (id_actionRole, nomTable, id_action, id_role) 
VALUES (3, 'utilisateur', 2, 2);
INSERT INTO actionRole (id_actionRole, nomTable, id_action, id_role) 
VALUES (4, 'utilisateur', 2, 2);

-- Insérer un utilisateur test
INSERT INTO utilisateur (id_user, username, password, id_actionRole, id_direction, id_status) 
VALUES (1, 'admin', 'admin123', 1, 1, 1);

INSERT INTO utilisateur (id_user, username, password, id_actionRole, id_direction, id_status) 
VALUES (2, 'client1', 'client123', 2, 2, 1);

-- Insérer un compte courant test
INSERT INTO compteCourant (id_compteCourant, solde, date_ouverture, id_user, id_status) 
VALUES (1, 5420.50, '2023-01-15', 2, 1);

-- Insérer des transactions test
INSERT INTO transaction (id_transaction, montant, date_transaction, id_compteCourant, id_type) 
VALUES (1, 1200.00, '2023-11-08', 1, 1);

INSERT INTO transaction (id_transaction, montant, date_transaction, id_compteCourant, id_type) 
VALUES (2, -50.00, '2023-11-10', 1, 2);