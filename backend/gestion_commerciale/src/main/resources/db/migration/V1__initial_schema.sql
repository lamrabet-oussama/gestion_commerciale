-- V1__init_admin_user.sql

-- Table Users si elle n'existe pas déjà
CREATE TABLE IF NOT EXISTS "Users" (
                                       "Cod" SERIAL PRIMARY KEY,
                                       "Login" VARCHAR(33) UNIQUE NOT NULL,
    "Pass" VARCHAR(255) NOT NULL,
    "role" VARCHAR(50) NOT NULL,
    "Dépot" INT,
    "Gsm" VARCHAR(20),
    "Note1" VARCHAR(20),
    "Note2" VARCHAR(20),
    "Note3" VARCHAR(20),
    "état" BOOLEAN DEFAULT TRUE
    );

-- Insérer un utilisateur ADMIN si non existant
INSERT INTO "Users" ("Login", "Pass", "role", "état")
SELECT 'admin', '$2y$10$5C5vNeGqUzrPP4QKL30KoeRmgDbAfvKdTog58OBTxmVTKH4IQgtAS', 'ADMIN', TRUE
    WHERE NOT EXISTS (
    SELECT 1 FROM "Users" WHERE "Login"='admin'
);

-- Table MesInfox si elle n'existe pas déjà
CREATE TABLE IF NOT EXISTS "MesInfox" (
                                          "num" SERIAL PRIMARY KEY,
                                          "Nom" VARCHAR(200),
    "Activité" VARCHAR(250),
    "Adres" VARCHAR(500),
    "piedPage" VARCHAR(500),
    "serial" VARCHAR(25),
    "fLogo" VARCHAR(255),
    "fCod" VARCHAR(25),
    "bNom" VARCHAR(500),
    "bSerial" VARCHAR(25),
    "bActivite" VARCHAR(250),
    "bAdresse" VARCHAR(500),
    "bLogo" VARCHAR(255),
    "bCod" VARCHAR(25),
    "note01" VARCHAR(500),
    "note02" VARCHAR(500)
    );

-- Insérer une ligne par défaut si non existante
INSERT INTO "MesInfox" ("Nom", "Activité", "Adres")
SELECT 'NomSociete', 'Activite', 'Adresse'
    WHERE NOT EXISTS (SELECT 1 FROM "MesInfox");
