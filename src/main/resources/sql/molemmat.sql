INSERT INTO Opiskelija (nimi) VALUES ('Platon');
INSERT INTO Opiskelija (nimi) VALUES ('Aristoteles');
INSERT INTO Opiskelija (nimi) VALUES ('Homeros');

INSERT INTO Alue (nimi) VALUES ('Yleinen höpinä'), ('Keilaus'), ('Tikanheitto');

INSERT INTO Viesti (opiskelija_id, alue_id, viesti_id, aika, teksti) VALUES
    (1, 1, NULL, datetime('now','-5 hours', 'localtime'), 'Everything is awesome'),
    (2, 1, 1, datetime('now','-4 hours', 'localtime'), 'Todellakin'),
    (3, 1, 1, datetime('now','-3 hours', 'localtime'), 'Niinhän sitä sanotaan');

INSERT INTO Viesti (opiskelija_id, alue_id, viesti_id, aika, teksti) VALUES
    (2, 1, NULL, datetime('now','-4 hours', 'localtime'), 'Tättärää'),
    (1, 1, 4, datetime('now','-2 hours', 'localtime'), 'Tetteree!'),
    (2, 1, 5, datetime('now','-1 hours', 'localtime'), 'Tätätätätätää');
