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

DROP TABLE IF EXISTS contacts CASCADE;
CREATE TABLE contacts (
  id UUID,
  owner VARCHAR(128) NOT NULL,
  first_name VARCHAR(128) NOT NULL,
  last_name VARCHAR(128),

  PRIMARY KEY (id),
  FOREIGN KEY (owner) REFERENCES users (login)
);

DROP TABLE IF EXISTS entries CASCADE;
CREATE TABLE entries (
  id UUID,
  contact_id UUID NOT NULL,
  phone VARCHAR(20) NOT NULL,

  PRIMARY KEY (id),
  FOREIGN KEY (contact_id) REFERENCES contacts (id)
);
