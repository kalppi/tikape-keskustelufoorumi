-- laitetaan foreign key supportti päälle ja ollaan vähän tarkempia asioiden suhteen eikä vain niellä niitä
PRAGMA foreign_keys = ON;
PRAGMA strict=ON;

DROP TABLE IF EXISTS Access_tokens;
DROP TABLE IF EXISTS Viesti;
DROP TABLE IF EXISTS Ketju;
DROP TABLE IF EXISTS Alue;
DROP TABLE IF EXISTS Opiskelija;

CREATE TABLE Opiskelija (id INTEGER PRIMARY KEY, nimi VARCHAR(255), pw_hash VARCHAR(88));
CREATE TABLE Alue (id INTEGER PRIMARY KEY, nimi VARCHAR(255));
CREATE TABLE Ketju (id INTEGER PRIMARY KEY, alue_id INTEGER, otsikko VARCHAR(255),
    FOREIGN KEY(alue_id) REFERENCES Alue(id));
CREATE TABLE Viesti (id INTEGER PRIMARY KEY, opiskelija_id INTEGER, ketju_id INTEGER, aika TEXT, teksti TEXT,
    FOREIGN KEY(opiskelija_id) REFERENCES Opiskelija(id),
    FOREIGN KEY(ketju_id) REFERENCES Ketju(id));
CREATE TABLE Access_tokens (id INTEGER PRIMARY KEY, token VARCHAR(32), opiskelija_id INTEGER,
    FOREIGN KEY(opiskelija_id) REFERENCES Opiskelija(id));

INSERT INTO Opiskelija (nimi, pw_hash) VALUES ('Platon', '');
INSERT INTO Opiskelija (nimi, pw_hash) VALUES ('Aristoteles', '');
INSERT INTO Opiskelija (nimi, pw_hash) VALUES ('Homeros', '');
INSERT INTO Opiskelija (nimi, pw_hash) VALUES ('Masa', '');
INSERT INTO Opiskelija (nimi, pw_hash) VALUES ('jarnoluu', '9xgGv2tFo/9kboNxa8b2qKEU+4HMVz6s4AHzrjCpLL8=FVZkdBGDlzLg2H+DGlsWcsHGoQ8xIOGknqtiuB5BnII=');

INSERT INTO Alue (nimi) VALUES ('Yleinen höpinä'), ('Keilaus'), ('Tikanheitto');

INSERT INTO Ketju (alue_id, otsikko) VALUES (1, 'Ketju1'), (1, 'Ketju2'), (2, 'Ketju3');

INSERT INTO Viesti (opiskelija_id, ketju_id, aika, teksti) VALUES
    (1, 1, datetime('now','-5 hours'), 'Everything is awesome'),
    (2, 1, datetime('now','-4 hours'), 'Todellakin'),
    (3, 1, datetime('now','-3 hours'), 'Niinhän sitä sanotaan');
  
INSERT INTO Viesti (opiskelija_id, ketju_id, aika, teksti) VALUES
    (2, 2, datetime('now','-4 hours'), 'Tättärää'),
    (1, 2, datetime('now','-2 hours'), 'Tetteree!'),
    (2, 2, datetime('now','-1 hours'), 'Tätätätätätää');

INSERT INTO Viesti (opiskelija_id, ketju_id, aika, teksti) VALUES
    (3, 3, datetime('now','-5 hours'), 'aaaaaaa'),
    (3, 3, datetime('now','-2 hours'), 'bbbbbb'),
    (2, 3, datetime('now'), 'ccc');

/*

alueet

SELECT a.id, a.nimi, COUNT(v.id) AS viestit, MAX(v.aika) AS viimeisin
        FROM Alue a
        LEFT JOIN Ketju k ON k.alue_id = a.id
        LEFT JOIN Viesti v ON k.id = v.ketju_id
        GROUP BY a.id;

alueen ketjut

SELECT k.id, k.otsikko, COUNT(v.id) as viestit, MAX(v.aika) AS viimeisin
        FROM Ketju k
        LEFT JOIN Viesti v ON k.id = v.ketju_id
        WHERE k.alue_id = 1
        GROUP BY k.id;

ketjun viestit

SELECT v.id, v.aika, v.teksti, o.nimi AS opiskelija
        FROM Viesti v
        LEFT JOIN Opiskelija o ON v.opiskelija_id = o.id
        WHERE v.ketju_id = 1
        ORDER BY v.aika ASC;

käyttäjän viestit

SELECT v.id, v.aika, v.teksti, k.otsikko
        FROM Viesti v
        LEFT JOIN Ketju k ON k.id = v.ketju_id
        WHERE opiskelija_id = 1;

*/