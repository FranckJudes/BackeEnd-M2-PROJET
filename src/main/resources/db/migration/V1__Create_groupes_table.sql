CREATE TABLE IF NOT EXISTS groupes (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   libele_groupe_utilisateur VARCHAR(255) UNIQUE NOT NULL,
   description_groupe_utilisateur VARCHAR(255),
   type VARCHAR(255) NOT NULL,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Création d'un trigger pour mettre à jour `updated_at`
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_groupes_updated_at
    BEFORE UPDATE ON groupes
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();