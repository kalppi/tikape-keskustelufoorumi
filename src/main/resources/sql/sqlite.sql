-- laitetaan foreign key supportti päälle
PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS Viesti;
DROP TABLE IF EXISTS Ketju;
DROP TABLE IF EXISTS Alue;
DROP TABLE IF EXISTS Opiskelija;


CREATE TABLE Opiskelija (id INTEGER PRIMARY KEY, nimi VARCHAR(255));
CREATE TABLE Alue (id INTEGER PRIMARY KEY, nimi VARCHAR(255));
CREATE TABLE Ketju (id INTEGER PRIMARY KEY, alue_id INTEGER, otsikko VARCHAR(255),
    FOREIGN KEY(alue_id) REFERENCES Alue(id));
CREATE TABLE Viesti (id INTEGER PRIMARY KEY, opiskelija_id INTEGER, ketju_id INTEGER, aika INTEGER, teksti TEXT,
    FOREIGN KEY(opiskelija_id) REFERENCES Opiskelija(id),
    FOREIGN KEY(ketju_id) REFERENCES Ketju(id));

INSERT INTO Opiskelija (nimi) VALUES ('Platon');
INSERT INTO Opiskelija (nimi) VALUES ('Aristoteles');
INSERT INTO Opiskelija (nimi) VALUES ('Homeros');

INSERT INTO Alue (nimi) VALUES ('Yleinen höpinä'), ('Keilaus'), ('Tikanheitto');

INSERT INTO Ketju (alue_id, otsikko) VALUES (1, 'jeee'), (1, 'jaaaaa');

INSERT INTO Viesti (opiskelija_id, ketju_id, aika, teksti) VALUES
    (1, 1, datetime('now','-5 hours', 'localtime'), 'Everything is awesome'),
    (2, 1, datetime('now','-4 hours', 'localtime'), 'Todellakin'),
    (3, 1, datetime('now','-3 hours', 'localtime'), 'Niinhän sitä sanotaan');

INSERT INTO Viesti (opiskelija_id, ketju_id, aika, teksti) VALUES
    (2, 2, datetime('now','-4 hours', 'localtime'), 'Tättärää'),
    (1, 2, datetime('now','-2 hours', 'localtime'), 'Tetteree!'),
    (2, 2, datetime('now','-1 hours', 'localtime'), 'Tätätätätätää');
