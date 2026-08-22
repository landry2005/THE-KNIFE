-- ============================================================
-- TheKnife - Script di creazione del database (dbTK)
-- DBMS: PostgreSQL
-- ============================================================

-- 1. PULIZIA INIZIALE
DROP VIEW IF EXISTS vista_valutazioni_ristoranti CASCADE;
DROP FUNCTION IF EXISTS verifica_ruolo_gestore() CASCADE;
DROP TABLE IF EXISTS preferiti CASCADE;
DROP TABLE IF EXISTS recensioni CASCADE;
DROP TABLE IF EXISTS ristoranti CASCADE;
DROP TABLE IF EXISTS utenti CASCADE;

-- ============================================================
-- 2. TABELLE PRINCIPALI
-- ============================================================

CREATE TABLE utenti (
    id              SERIAL PRIMARY KEY,
    nome            VARCHAR(100)    NOT NULL,
    cognome         VARCHAR(100)    NOT NULL,
    username           VARCHAR(150)    NOT NULL UNIQUE,
    password_hash   VARCHAR(255)    NOT NULL,
    data_nascita    DATE,
    citta_domicilio VARCHAR(100)    NOT NULL,
    ruolo           VARCHAR(15)     NOT NULL CHECK (ruolo IN ('cliente', 'ristoratore'))
    domanda_sicurezza VARCHAR(255),
    risposta_sicurezza VARCHAR(255)
);

CREATE TABLE ristoranti (
    id                     SERIAL PRIMARY KEY,
    nome                   VARCHAR(200)    NOT NULL,
    nazione                VARCHAR(100)    NOT NULL,
    citta                  VARCHAR(100)    NOT NULL,
    indirizzo              VARCHAR(255),
    latitudine             NUMERIC(9,6),                      
    longitudine            NUMERIC(9,6),
    fascia_prezzo          NUMERIC(8,2)    NOT NULL CHECK (fascia_prezzo >= 0), 
    delivery               BOOLEAN         NOT NULL DEFAULT FALSE,
    prenotazione_online    BOOLEAN         NOT NULL DEFAULT FALSE,
    tipo_cucina            VARCHAR(100)    NOT NULL,
    id_gestore             INTEGER         REFERENCES utenti(id) ON DELETE SET NULL,
    -- Vincolo per impedire duplicati (stesso nome e stesso indirizzo)
    CONSTRAINT uq_ristorante_nome_indirizzo UNIQUE (nome, indirizzo)
);

CREATE TABLE recensioni (
    id              SERIAL PRIMARY KEY,
    id_utente       INTEGER         NOT NULL REFERENCES utenti(id) ON DELETE CASCADE,
    id_ristorante   INTEGER         NOT NULL REFERENCES ristoranti(id) ON DELETE CASCADE,
    stelle          SMALLINT        NOT NULL CHECK (stelle BETWEEN 1 AND 5),
    testo           TEXT,
    data_creazione  TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    risposta        TEXT,
    data_risposta   TIMESTAMPTZ,
    CONSTRAINT uq_recensione_utente_ristorante UNIQUE (id_utente, id_ristorante)
);

CREATE TABLE preferiti (
    id_utente       INTEGER NOT NULL REFERENCES utenti(id) ON DELETE CASCADE,
    id_ristorante   INTEGER NOT NULL REFERENCES ristoranti(id) ON DELETE CASCADE,
    PRIMARY KEY (id_utente, id_ristorante)
);

-- ============================================================
-- 3. LOGICA AVANZATA (TRIGGER)
-- ============================================================

CREATE OR REPLACE FUNCTION verifica_ruolo_gestore() 
RETURNS TRIGGER AS $$ BEGIN
    IF NEW.id_gestore IS NOT NULL THEN
        IF NOT EXISTS (
            SELECT 1 FROM utenti WHERE id = NEW.id_gestore AND ruolo = 'ristoratore'
        ) THEN
            RAISE EXCEPTION 'Errore: l''utente con id % non è un ristoratore valido.', NEW.id_gestore;
        END IF;
    END IF;
    RETURN NEW;
END;
 $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_verifica_ruolo_gestore
    BEFORE INSERT OR UPDATE ON ristoranti
    FOR EACH ROW EXECUTE FUNCTION verifica_ruolo_gestore();

-- ============================================================
-- 4. OTTIMIZZAZIONE (INDICI)
-- ============================================================
CREATE INDEX idx_ristoranti_citta        ON ristoranti (citta);
CREATE INDEX idx_ristoranti_cucina       ON ristoranti (tipo_cucina);
CREATE INDEX idx_ristoranti_prezzo       ON ristoranti (fascia_prezzo);
CREATE INDEX idx_ristoranti_gestore      ON ristoranti (id_gestore);
CREATE INDEX idx_recensioni_ristorante   ON recensioni (id_ristorante);
CREATE INDEX idx_recensioni_utente       ON recensioni (id_utente);

-- ============================================================
-- 5. VISTE DI COMODO
-- ============================================================
CREATE OR REPLACE VIEW vista_valutazioni_ristoranti AS
SELECT
    r.id,
    r.nome,
    COUNT(rec.id)                              AS numero_recensioni,
    COALESCE(ROUND(AVG(rec.stelle), 2), 0)     AS media_stelle
FROM ristoranti r
LEFT JOIN recensioni rec ON rec.id_ristorante = r.id
GROUP BY r.id, r.nome;