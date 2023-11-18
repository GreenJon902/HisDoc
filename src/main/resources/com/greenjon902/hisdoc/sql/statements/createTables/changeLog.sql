CREATE TABLE IF NOT EXISTS {prefix}ChangeLog (
  cid       INTEGER      NOT NULL AUTO_INCREMENT,
  eid       INTEGER      NOT NULL  REFERENCES {prefix}Event(eid),
  description       LONGTEXT NOT NULL,
  authorUid INTEGER      NOT NULL  REFERENCES {prefix}Person(uid),
  date      TIMESTAMP    NOT NULL,
  PRIMARY KEY(cid)
);