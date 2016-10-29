
DROP TABLE IF EXISTS Access_tokens;
DROP TABLE IF EXISTS Messages;
DROP TABLE IF EXISTS Threads;
DROP TABLE IF EXISTS Categories;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users (id SERIAL PRIMARY KEY, name VARCHAR(20) UNIQUE, pw_hash VARCHAR(88), admin BOOLEAN, registered TIMESTAMP WITH TIME ZONE DEFAULT (now()));
CREATE TABLE Categories (id SERIAL PRIMARY KEY, name VARCHAR(255) UNIQUE);
CREATE TABLE Threads (id SERIAL PRIMARY KEY, category_id INTEGER, title VARCHAR(255),
    FOREIGN KEY(category_id) REFERENCES Categories(id));
CREATE TABLE Messages (id SERIAL PRIMARY KEY, user_id INTEGER, thread_id INTEGER, sent TIMESTAMP WITH TIME ZONE DEFAULT (now()), text TEXT,
    FOREIGN KEY(user_id) REFERENCES Users(id),
    FOREIGN KEY(thread_id) REFERENCES Threads(id));
CREATE TABLE Access_tokens (id SERIAL PRIMARY KEY, token VARCHAR(44), user_id INTEGER,
    FOREIGN KEY(user_id) REFERENCES Users(id));

INSERT INTO Users (name, pw_hash, admin) VALUES ('admin', 'q1P0KA5MQwZ4DqpsSvFOxb+MTswRREjbc9gqlajedXE=ZX6KXGEyLbzmK/0GyqCJhyieCdNS/kCLrGfbbmvCDN4=', TRUE);
INSERT INTO Users (name, pw_hash, admin) VALUES ('JAVA-MAN', '', FALSE);

INSERT INTO Categories (name) VALUES ('Yleinen höpinä'), ('Java fan club');

INSERT INTO Threads (category_id, title) VALUES (2, 'JAVA 4-EVER');

INSERT INTO Messages (user_id, thread_id, sent, text) VALUES
    (2, 1, now(), 'https://www.youtube.com/watch?v=yl1f1-Da0OI');