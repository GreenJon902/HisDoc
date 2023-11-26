CREATE TABLE IF NOT EXISTS {prefix}EventPersonRelation (
  eurid INTEGER NOT NULL AUTO_INCREMENT,
  eid   INTEGER NOT NULL  REFERENCES {prefix}Event(eid),
  pid   INTEGER NOT NULL  REFERENCES {prefix}Person(pid),
  UNIQUE (eid, pid),
  PRIMARY KEY(eurid)
);