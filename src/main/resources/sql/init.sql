
DROP TABLE IF EXISTS Access_tokens;
DROP TABLE IF EXISTS Messages;
DROP TABLE IF EXISTS Threads;
DROP TABLE IF EXISTS Categories;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users (id SERIAL PRIMARY KEY, name VARCHAR(20) UNIQUE, pw_hash VARCHAR(88), admin BOOLEAN);
CREATE TABLE Categories (id SERIAL PRIMARY KEY, name VARCHAR(255) UNIQUE);
CREATE TABLE Threads (id SERIAL PRIMARY KEY, category_id INTEGER, title VARCHAR(255),
    FOREIGN KEY(category_id) REFERENCES Categories(id));
CREATE TABLE Messages (id SERIAL PRIMARY KEY, user_id INTEGER, thread_id INTEGER, sent TIMESTAMP WITH TIME ZONE DEFAULT (now()), text TEXT,
    FOREIGN KEY(user_id) REFERENCES Users(id),
    FOREIGN KEY(thread_id) REFERENCES Threads(id));
CREATE TABLE Access_tokens (id SERIAL PRIMARY KEY, token VARCHAR(44), user_id INTEGER,
    FOREIGN KEY(user_id) REFERENCES Users(id));

INSERT INTO Users (name, pw_hash, admin) VALUES ('JAVA-MAN', '', FALSE);
INSERT INTO Users (name, pw_hash, admin) VALUES ('Platon', '', FALSE);
INSERT INTO Users (name, pw_hash, admin) VALUES ('Aristoteles', '', FALSE);
INSERT INTO Users (name, pw_hash, admin) VALUES ('Homeros', '', FALSE);
INSERT INTO Users (name, pw_hash, admin) VALUES ('Masa', '', FALSE);
INSERT INTO Users (name, pw_hash, admin) VALUES ('jarnoluu', '9xgGv2tFo/9kboNxa8b2qKEU+4HMVz6s4AHzrjCpLL8=FVZkdBGDlzLg2H+DGlsWcsHGoQ8xIOGknqtiuB5BnII=', TRUE);
INSERT INTO Users (name, pw_hash, admin) VALUES ('admin', 'q1P0KA5MQwZ4DqpsSvFOxb+MTswRREjbc9gqlajedXE=ZX6KXGEyLbzmK/0GyqCJhyieCdNS/kCLrGfbbmvCDN4=', TRUE);

INSERT INTO Categories (name) VALUES ('Yleinen höpinä'), ('Keilaus'), ('Tikanheitto'), ('Java fan club');

INSERT INTO Threads (category_id, title) VALUES (1, 'Ketju1'), (1, 'Ketju2'), (2, 'Ketju3'), (4, 'JAVA 4-EVER');

INSERT INTO Messages (user_id, thread_id, sent, text) VALUES
    (1, 1, now() - INTERVAL '10 hour', 'ensimmäinen 1'),
    (1, 1, now() - INTERVAL '8 hour', 'Tetteree!'),
    (2, 1, now() - INTERVAL '7 hour', 'Todellakin'),
    (3, 1, now() - INTERVAL '3 hour', 'Niinhän sitä sanotaan');
  
INSERT INTO Messages (user_id, thread_id, sent, text) VALUES
    (2, 2, now() - INTERVAL '4 hour', 'Tättärää <b>asdasd</b>'),
    (1, 2, now() - INTERVAL '3 hour', 'Tetteree!'),
    (1, 2, now() - INTERVAL '2 hour', 'Tetteree!'),
    (1, 2, now() - INTERVAL '2 hour', 'Tetteree!'),
    (1, 2, now() - INTERVAL '2 hour', 'Tetteree!'),
    (2, 2, now() - INTERVAL '1 hour', 'viimeisin 1');

INSERT INTO Messages (user_id, thread_id, sent, text) VALUES
    (3, 3, now() - INTERVAL '12 hour', 'ensimmäinen 2'),
    (3, 3, now() - INTERVAL '8 hour', 'bbbbbb'),
    (3, 3, now() - INTERVAL '4 hour', 'bbbbbb'),
    (2, 3, now(), 'viimeisin 2');

INSERT INTO Messages (user_id, thread_id, sent, text) VALUES
    (1, 4, now(), 'https://www.youtube.com/watch?v=yl1f1-Da0OI');