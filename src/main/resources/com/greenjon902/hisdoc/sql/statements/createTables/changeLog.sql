CREATE TABLE IF NOT EXISTS {prefix}ChangeLog (
  cid       INTEGER      NOT NULL AUTO_INCREMENT,
  eid       INTEGER      NOT NULL  REFERENCES {prefix}Event(eid),
  description       LONGTEXT NOT NULL,
  authorPid INTEGER      NOT NULL  REFERENCES {prefix}Person(pid),
  date      BIGINT UNSIGNED    NOT NULL,  -- in seconds
  PRIMARY KEY(cid)
);