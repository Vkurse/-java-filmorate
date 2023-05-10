MERGE INTO mpa_type (rating_mpa_id, name)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

MERGE INTO genre_type (genre_id, name)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

INSERT INTO users (email, login, name, birthday) VALUES ('email1@email,ru', 'login1', 'name1', '1990-01-10');
INSERT INTO users (email, login, name, birthday) VALUES ('email2@email,ru', 'login2', 'name2', '1990-01-20');
INSERT INTO users (email, login, name, birthday) VALUES ('email3@email,ru', 'login3', 'name3', '1990-01-30');
INSERT INTO users (email, login, name, birthday) VALUES ('email4@email,ru', 'login4', 'name4', '1990-02-01');
INSERT INTO users (email, login, name, birthday) VALUES ('email5@email,ru', 'login5', 'name5', '1990-02-01');

INSERT INTO DIRECTORS (NAME)
    VALUES ('Director1'),
           ('Director2'),
           ('Director3');


INSERT INTO films (name, description, release_date, duration, rating_mpa_id) VALUES('name1', 'description1', '2000-12-12', 120, 1);
INSERT INTO films (name, description, release_date, duration, rating_mpa_id) VALUES('name2', 'description2', '2000-12-13', 180, 2);

INSERT INTO DIRECTOR_FILMS(FILM_ID, DIRECTOR_ID)
    VALUES (1, 1),
           (1, 3),
           (2, 1);