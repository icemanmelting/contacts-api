DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
  login VARCHAR(128),
  password VARCHAR(64) NOT NULL,
  salt VARCHAR(64),

  PRIMARY KEY (login)
);

DROP TABLE IF EXISTS sessions CASCADE;
CREATE TABLE sessions (
  id UUID,
  login VARCHAR(128) NOT NULL,
  seen TIMESTAMP NOT NULL,

  PRIMARY KEY (id),
  FOREIGN KEY (login) REFERENCES users (login) ON DELETE CASCADE
);

DROP TABLE IF EXISTS contacts CASCADE;
CREATE TABLE contacts (
  id UUID,
  owner VARCHAR(128) NOT NULL,
  first_name VARCHAR(128) NOT NULL,
  last_name VARCHAR(128) NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,

  PRIMARY KEY (id),
  FOREIGN KEY (owner) REFERENCES users (login) ON DELETE CASCADE
);

DROP TABLE IF EXISTS entries CASCADE;
CREATE TABLE entries (
  phone VARCHAR(20) NOT NULL,
  contact_id UUID NOT NULL,
  region VARCHAR(3),

  PRIMARY KEY (phone, contact_id),
  FOREIGN KEY (contact_id) REFERENCES contacts (id) ON DELETE CASCADE
);
