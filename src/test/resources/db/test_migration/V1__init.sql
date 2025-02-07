CREATE TABLE users
(
    id       IDENTITY PRIMARY KEY,
    login    VARCHAR(15) UNIQUE NOT NULL,
    password VARCHAR(255)       NOT NULL
);

CREATE TABLE sessions
(
    id         VARCHAR(255) PRIMARY KEY,
    user_id    LONG    NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_sessions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE locations
(
    id        IDENTITY PRIMARY KEY,
    name      VARCHAR(255)   NOT NULL,
    user_id   LONG         NOT NULL,
    latitude  DECIMAL(10, 4) NOT NULL,
    longitude DECIMAL(10, 4) NOT NULL,
    CONSTRAINT fk_location_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);