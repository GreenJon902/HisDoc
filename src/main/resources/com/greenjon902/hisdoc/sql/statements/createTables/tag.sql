CREATE TABLE IF NOT EXISTS {prefix}Tag (
  tid           INTEGER      NOT NULL AUTO_INCREMENT,
  name          VARCHAR(255) NOT NULL,
  description    LONGTEXT NOT NULL,
  color         INTEGER      NOT NULL,  -- _RGB, we have 8 unused bits
  PRIMARY KEY(tid)
);