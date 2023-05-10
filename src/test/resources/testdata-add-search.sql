INSERT INTO mpa_type (rating_mpa_id, name)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

INSERT INTO genre_type (genre_id, name)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
    VALUES  ('mail@yandex.ru', 'doloreUpdate', 'est adipisicing', '1976-09-20'),
            ('friend@mail.ru', 'friend', 'friend adipisicing', '1976-08-20'),
            ('friend@common.ru', 'common', 'common', '2000-08-20');

-- для тестов поиска

INSERT INTO DIRECTORS (NAME)
    VALUES('Дэн Кван'),
    ('Скотт Дерриксон');

-- для однокоренных фильмов
INSERT INTO films (name, description, release_date, duration, rating_mpa_id)
    VALUES ('Доктор Стрендж', 'За гранью сознания лежит новая реальность', '2016-10-31', 115, 3),
     ('Доктор Лиза', 'Один день, чтобы спасти всех', '2020-10-22', 120, 5),
     ('Удивительное путешествие доктора Дулиттла', 'He''s just not a people person', '2020-04-16', 101, 2);

-- для поиска по режиссеру
INSERT INTO films (name, description, release_date, duration, rating_mpa_id)
    VALUES ('Всё везде и сразу', 'Судьба мультивселенной — в руках владелицы прачечной', '2022-04-07', 139, 4),
           ('Человек — швейцарский нож', 'Труп познается в беде', '2016-06-30', 97, 4);

INSERT INTO DIRECTOR_FILMS (film_id, director_id)
    VALUES (1, 2),
           (4, 1),
           (5, 1);

INSERT INTO LIKES (FILM_ID, USER_ID)
    VALUES (4, 1),
           (5, 1),
           (5, 2),
           (1, 1),
           (1, 2),
           (1, 3),
           (2, 1),
           (2, 2);


INSERT INTO GENRE (FILM_ID, GENRE_ID)
VALUES (1, 6),
       (2, 5),
       (3, 1),
       (4, 6),
       (4, 1),
       (5, 2);