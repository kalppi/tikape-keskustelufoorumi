-- laitetaan foreign key supportti päälle
PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS Opiskelija;
DROP TABLE IF EXISTS Alue;
DROP TABLE IF EXISTS Viesti;

CREATE TABLE Opiskelija (id INTEGER PRIMARY KEY, nimi VARCHAR(255));
CREATE TABLE Alue (id INTEGER PRIMARY KEY, nimi VARCHAR(255));

CREATE TABLE Viesti (id INTEGER PRIMARY KEY, opiskelija_id INTEGER, alue_id INTEGER, viesti_id INTEGER, aika INTEGER, teksti TEXT,
    FOREIGN KEY(opiskelija_id) REFERENCES Opiskelija(id),
    FOREIGN KEY(alue_id) REFERENCES Alue(id),
    FOREIGN KEY(viesti_id) REFERENCES Viesti(id));

