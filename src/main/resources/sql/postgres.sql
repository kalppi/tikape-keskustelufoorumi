DROP TABLE IF EXISTS Viesti;
DROP TABLE IF EXISTS Ketju;
DROP TABLE IF EXISTS Alue;
DROP TABLE IF EXISTS Opiskelija;

CREATE TABLE Opiskelija (id SERIAL PRIMARY KEY, nimi VARCHAR(255));
CREATE TABLE Alue (id SERIAL PRIMARY KEY, nimi VARCHAR(255));
CREATE TABLE Ketju (id SERIAL PRIMARY KEY, alue_id INTEGER, otsikko VARCHAR(255),
    FOREIGN KEY(alue_id) REFERENCES Alue(id));
CREATE TABLE Viesti (id SERIAL PRIMARY KEY, opiskelija_id INTEGER, ketju_id INTEGER, aika TIMESTAMP, teksti TEXT,
    FOREIGN KEY(opiskelija_id) REFERENCES Opiskelija(id),
    FOREIGN KEY(ketju_id) REFERENCES Ketju(id));

INSERT INTO Opiskelija (nimi) VALUES ('Platon');
INSERT INTO Opiskelija (nimi) VALUES ('Aristoteles');
INSERT INTO Opiskelija (nimi) VALUES ('Homeros');
INSERT INTO Opiskelija (nimi) VALUES ('Masa');

INSERT INTO Alue (nimi) VALUES ('Yleinen höpinä'), ('Keilaus'), ('Tikanheitto');

INSERT INTO Ketju (alue_id, otsikko) VALUES (1, 'Ketju1'), (1, 'Ketju2'), (2, 'Ketju3');

INSERT INTO Viesti (opiskelija_id, ketju_id, aika, teksti) VALUES
    (1, 1, now() - INTERVAL '5 hour', 'Everything is awesome'),
    (2, 1, now() - INTERVAL '4 hour', 'Todellakin'),
    (3, 1, now() - INTERVAL '3 hour', 'Niinhän sitä sanotaan');
  
INSERT INTO Viesti (opiskelija_id, ketju_id, aika, teksti) VALUES
    (2, 2, now() - INTERVAL '4 hour', 'Tättärää'),
    (1, 2, now() - INTERVAL '2 hour', 'Tetteree!'),
    (2, 2, now() - INTERVAL '1 hour', 'Tätätätätätää');

INSERT INTO Viesti (opiskelija_id, ketju_id, aika, teksti) VALUES
    (3, 3, now() - INTERVAL '5 hour', 'aaaaaaa'),
    (3, 3, now() - INTERVAL '25 hour', 'bbbbbb'),
    (2, 3, now(), 'ccc');