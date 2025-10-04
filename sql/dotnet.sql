CREATE DATABASE banque_depot;

-- Utilisateur (si tu veux un user dédié)
CREATE USER banque_user WITH ENCRYPTED PASSWORD 'banque_pass';

-- Donner accès
GRANT ALL PRIVILEGES ON DATABASE banque_depot TO banque_user;

\c banque_depot