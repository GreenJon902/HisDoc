CREATE TABLE IF NOT EXISTS {prefix}Person (
  pid      INTEGER      NOT NULL AUTO_INCREMENT,
  personType ENUM({personTypes}) NOT NULL,  -- Filled with values from the PersonType enum in java
  personData VARCHAR(36),
  UNIQUE (personType, personData),
  PRIMARY KEY (pid)
);