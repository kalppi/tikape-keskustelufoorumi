-- laitetaan foreign key supportti päälle ja ollaan vähän tarkempia asioiden suhteen eikä vain niellä niitä
PRAGMA foreign_keys = ON;
PRAGMA strict=ON;

DROP TABLE IF EXISTS Access_tokens;
DROP TABLE IF EXISTS Messages;
DROP TABLE IF EXISTS Threads;
DROP TABLE IF EXISTS Categories;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users (id INTEGER PRIMARY KEY, name VARCHAR(255), pw_hash VARCHAR(88), admin INTEGER);
CREATE TABLE Categories (id INTEGER PRIMARY KEY, name VARCHAR(255));
CREATE TABLE Threads (id INTEGER PRIMARY KEY, category_id INTEGER, title VARCHAR(255),
    FOREIGN KEY(category_id) REFERENCES Categories(id));
CREATE TABLE Messages (id INTEGER PRIMARY KEY, user_id INTEGER, thread_id INTEGER, sent TEXT, text TEXT,
    FOREIGN KEY(user_id) REFERENCES Users(id),
    FOREIGN KEY(thread_id) REFERENCES Threads(id));
CREATE TABLE Access_tokens (id INTEGER PRIMARY KEY, token VARCHAR(44), user_id INTEGER,
    FOREIGN KEY(user_id) REFERENCES Users(id));

INSERT INTO Users (name, pw_hash, admin) VALUES ('Platon', '', 0);
INSERT INTO Users (name, pw_hash, admin) VALUES ('Aristoteles', '', 0);
INSERT INTO Users (name, pw_hash, admin) VALUES ('Homeros', '', 0);
INSERT INTO Users (name, pw_hash, admin) VALUES ('Masa', '', 0);
INSERT INTO Users (name, pw_hash, admin) VALUES ('jarnoluu', '9xgGv2tFo/9kboNxa8b2qKEU+4HMVz6s4AHzrjCpLL8=FVZkdBGDlzLg2H+DGlsWcsHGoQ8xIOGknqtiuB5BnII=', 1);
INSERT INTO Users (name, pw_hash, admin) VALUES ('admin', 'q1P0KA5MQwZ4DqpsSvFOxb+MTswRREjbc9gqlajedXE=ZX6KXGEyLbzmK/0GyqCJhyieCdNS/kCLrGfbbmvCDN4=', 1);

INSERT INTO Categories (name) VALUES ('Yleinen höpinä'), ('Keilaus'), ('Tikanheitto');

INSERT INTO Threads (category_id, title) VALUES (1, 'Ketju1'), (1, 'Ketju2'), (2, 'Ketju3');

INSERT INTO Messages (user_id, thread_id, sent, text) VALUES
    (1, 1, datetime('now','-5 hours'), 'Everything is awesome'),
    (2, 1, datetime('now','-4 hours'), 'Todellakin'),
    (3, 1, datetime('now','-3 hours'), 'Niinhän sitä sanotaan');
  
INSERT INTO Messages (user_id, thread_id, sent, text) VALUES
    (2, 2, datetime('now','-4 hours'), 'Tättärää'),
    (1, 2, datetime('now','-2 hours'), 'Tetteree!'),
    (2, 2, datetime('now','-1 hours'), 'Tätätätätätää');

INSERT INTO Messages (user_id, thread_id, sent, text) VALUES
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