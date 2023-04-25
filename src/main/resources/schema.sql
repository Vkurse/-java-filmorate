DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS mpa_type
(
    rating_mpa_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name          VARCHAR(40)

);

CREATE TABLE IF NOT EXISTS directors
(
    director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    film_id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    description   VARCHAR(200) NOT NULL,
    release_date  TIMESTAMP    NOT NULL,
    duration      INTEGER         NOT NULL,
    rating_mpa_id INTEGER REFERENCES mpa_type (rating_mpa_id)
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    VARCHAR(50) NOT NULL,
    login    VARCHAR(50) NOT NULL,
    name     VARCHAR(50),
    birthday DATE        NOT NULL

);

CREATE TABLE IF NOT EXISTS likes
(
    like_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id INTEGER NOT NULL REFERENCES films (film_id),
    user_id INTEGER NOT NULL REFERENCES users (user_id)

);

CREATE TABLE IF NOT EXISTS genre_type
(
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     VARCHAR(40) NOT NULL

);

CREATE TABLE IF NOT EXISTS genre
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id  INTEGER NOT NULL REFERENCES films (film_id),
    genre_id INTEGER NOT NULL REFERENCES genre_type (genre_id)

);

CREATE TABLE IF NOT EXISTS director_films
(
  film_id INTEGER NOT NULL REFERENCES films (film_id),
  director_id INTEGER NOT NULL REFERENCES directors (director_id),
  PRIMARY KEY (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS friends
(
    relationship_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id         INTEGER NOT NULL REFERENCES users (user_id),
    friend_id       INTEGER NOT NULL REFERENCES users (user_id)

);