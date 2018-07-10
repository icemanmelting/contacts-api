DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
  login VARCHAR(128),
  password CHAR(64),
  salt VARCHAR(64),

  PRIMARY KEY (login)
);

DROP TABLE IF EXISTS sessions CASCADE;
CREATE TABLE sessions (
  id UUID,
  login VARCHAR(128) NOT NULL,
  seen TIMESTAMP NOT NULL,

  PRIMARY KEY (id),
  FOREIGN KEY (login) REFERENCES users (login)
);

