-- Supprimer la contrainte de clé primaire
ALTER TABLE groupes DROP CONSTRAINT groupes_pkey;

-- Modifier le type de la colonne id
ALTER TABLE groupes ALTER COLUMN id TYPE UUID USING id::uuid;

-- Ajouter une nouvelle contrainte de clé primaire
ALTER TABLE groupes ADD PRIMARY KEY (id);